package com.wrathur.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wrathur.api.client.InstructionServiceClient;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseDTO;
import com.wrathur.course.domain.po.Course;
import com.wrathur.course.domain.po.Homework;
import com.wrathur.course.domain.po.StudentCourse;
import com.wrathur.course.domain.po.StudentHomework;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.mapper.CourseMapper;
import com.wrathur.course.mapper.HomeworkMapper;
import com.wrathur.course.mapper.StudentCourseMapper;
import com.wrathur.course.mapper.StudentHomeworkMapper;
import com.wrathur.course.service.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;
    private final HomeworkMapper homeworkMapper;
    private final StudentHomeworkMapper studentHomeworkMapper;

    @Setter
    private InstructionServiceClient instructionServiceClient;

    // 创建课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setReviewStatus("PENDING");
        course.setIsDeleted(false);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
    }

    // 修改课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCourse(Integer id, CourseDTO courseDTO) {
        Course course = courseMapper.selectById(id);
        BeanUtils.copyProperties(courseDTO, course);
        course.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<Course> modifyWrapper = new UpdateWrapper<>();
        modifyWrapper.eq("id", id);
        courseMapper.update(course, modifyWrapper);
    }

    // 删除课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Integer id) {
        // 查询该课程，将其从课程表逻辑删除
        courseMapper.update(null,
                new LambdaUpdateWrapper<Course>()
                        .eq(Course::getId, id)
                        .set(Course::getIsDeleted, true)
                        .set(Course::getDeleteTime, LocalDateTime.now()));

        // 查询选修了该课程的所有学生，将其从学生课程表逻辑删除
        studentCourseMapper.update(null,
                new LambdaUpdateWrapper<StudentCourse>()
                        .eq(StudentCourse::getCourseId, id)
                        .set(StudentCourse::getIsDeleted, true)
                        .set(StudentCourse::getDeleteTime, LocalDateTime.now()));

        // 远程调用教学服务获取该课程所有未删除的作业
        List<Integer> homeworkIds = instructionServiceClient.getHomeworkIdsByCourseId(id);

        // 查询该课程的所有作业，将其从作业表、学生作业表逻辑删除
        if (homeworkIds != null && !homeworkIds.isEmpty()) {
            // 删除作业表项
            homeworkMapper.update(null,
                    new LambdaUpdateWrapper<Homework>()
                            .in(Homework::getId, homeworkIds)
                            .set(Homework::getIsDeleted, true)
                            .set(Homework::getDeleteTime, LocalDateTime.now()));

            // 删除学生作业关联表项
            studentHomeworkMapper.update(null,
                    new LambdaUpdateWrapper<StudentHomework>()
                            .in(StudentHomework::getHomeworkId, homeworkIds)
                            .set(StudentHomework::getIsDeleted, true)
                            .set(StudentHomework::getDeleteTime, LocalDateTime.now()));
        }
    }

    // 获取课程分页
    @Override
    public IPage<CourseVO> getCoursePages(CourseQueryDTO courseQueryDTO) {
        // 构建分页对象
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Course> pageWrapper = new LambdaQueryWrapper<>();

        // 模糊查询名称
        if (courseQueryDTO.getName() != null && !courseQueryDTO.getName().isEmpty()) {
            pageWrapper.like(Course::getName, courseQueryDTO.getName());
        }
        // 精确查询审核状态
        if (courseQueryDTO.getReviewStatus() != null) {
            pageWrapper.eq(Course::getReviewStatus, courseQueryDTO.getReviewStatus());
        }
        // 精确查询学科
        if (courseQueryDTO.getSubjectId() != null) {
            pageWrapper.eq(Course::getSubjectId, courseQueryDTO.getSubjectId());
        }
        // 精确查询类型
        if (courseQueryDTO.getTypeId() != null) {
            pageWrapper.eq(Course::getTypeId, courseQueryDTO.getTypeId());
        }
        // 范围查询创建时间
        if (courseQueryDTO.getStartCreateTime() != null) {
            pageWrapper.ge(Course::getCreateTime, courseQueryDTO.getStartCreateTime());
        }
        if (courseQueryDTO.getEndCreateTime() != null) {
            pageWrapper.le(Course::getCreateTime, courseQueryDTO.getEndCreateTime());
        }
        // 过滤已删除课程
        pageWrapper.eq(Course::getIsDeleted, false);

        // 执行分页查询
        IPage<Course> coursePage = courseMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<CourseVO> courseVOS = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO courseVO = new CourseVO();
                    BeanUtils.copyProperties(course, courseVO);
                    courseVO.setCreateTime(course.getCreateTime());
                    courseVO.setUpdateTime(course.getUpdateTime());
                    courseVO.setReviewTime(course.getReviewTime());
                    courseVO.setDeleteTime(course.getDeleteTime());
                    return courseVO;
                }).collect(Collectors.toList());

        // 构建返回的分页VO
        IPage<CourseVO> resultPage = new Page<>();
        resultPage.setRecords(courseVOS);
        resultPage.setTotal(coursePage.getTotal());
        resultPage.setPages(coursePage.getPages());
        resultPage.setCurrent(coursePage.getCurrent());
        resultPage.setSize(coursePage.getSize());
        return resultPage;
    }

    // 获取课程详情
    @Override
    public CourseVO getCourseDetail(Integer id) {
        Course course = courseMapper.selectById(id);
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);

        // 特殊属性需要额外赋值
        courseVO.setCreateTime(course.getCreateTime());
        courseVO.setUpdateTime(course.getUpdateTime());
        courseVO.setReviewTime(course.getReviewTime());
        courseVO.setDeleteTime(course.getDeleteTime());
        return courseVO;
    }

    // 审核课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewCourse(String reviewStatus, CourseDTO courseDTO) {
        Course course = new Course();
        course.setReviewStatus(reviewStatus);
        course.setReviewTime(LocalDateTime.now());
        if (reviewStatus.equals("REJECTED")) {
            course.setRejectedReason(courseDTO.getRejectedReason());
        }

        UpdateWrapper<Course> reviewWrapper = new UpdateWrapper<>();
        reviewWrapper.eq("id", courseDTO.getId());
        courseMapper.update(course, reviewWrapper);
    }

    // 选修课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectCourse(Integer studentId, Integer courseId) {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(courseId);
        studentCourse.setProgress(0);
        studentCourse.setCreateTime(LocalDateTime.now());
        studentCourse.setUpdateTime(LocalDateTime.now());
        studentCourse.setSelectTime(LocalDateTime.now());
        studentCourseMapper.insert(studentCourse);

        // 远程调用教学服务获取该课程所有未删除的作业
        List<Integer> homeworkIds = instructionServiceClient.getHomeworkIdsByCourseId(courseId);

        // 为每个作业创建学生作业关联表项
        studentHomeworkMapper.insertBatch(homeworkIds.stream()
                .map(homeworkId -> {
                    StudentHomework studentHomework = new StudentHomework();
                    studentHomework.setStudentId(studentId);
                    studentHomework.setHomeworkId(homeworkId);
                    studentHomework.setReviewStatus("UNSUBMITTED");
                    studentHomework.setIsDeleted(false);
                    studentHomework.setCreateTime(LocalDateTime.now());
                    studentHomework.setUpdateTime(LocalDateTime.now());
                    return studentHomework;
                }).collect(Collectors.toList()));
    }

    // 退选课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deselectCourse(Integer studentId, Integer courseId) {
        // 查询该学生，将其从学生课程表逻辑删除
        studentCourseMapper.update(null,
                new LambdaUpdateWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, studentId)
                        .eq(StudentCourse::getCourseId, courseId)
                        .set(StudentCourse::getIsDeleted, true)
                        .set(StudentCourse::getDeleteTime, LocalDateTime.now()));

        // 远程调用教学服务获取该课程所有未删除的作业
        List<Integer> homeworkIds = instructionServiceClient.getHomeworkIdsByCourseId(courseId);

        // 查询该课程的所有作业，将其从学生作业表逻辑删除
        if (homeworkIds != null && !homeworkIds.isEmpty()) {
            // 删除学生作业关联表项
            studentHomeworkMapper.update(null,
                    new LambdaUpdateWrapper<StudentHomework>()
                            .eq(StudentHomework::getStudentId, studentId)
                            .in(StudentHomework::getHomeworkId, homeworkIds)
                            .set(StudentHomework::getIsDeleted, true)
                            .set(StudentHomework::getDeleteTime, LocalDateTime.now()));
        }
    }

    // 评定课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateCourse(BigDecimal score, StudentCourseDTO studentCourseDTO) {
        StudentCourse studentCourse = new StudentCourse();
        BeanUtils.copyProperties(studentCourseDTO, studentCourse);
        studentCourse.setScore(score);
        studentCourse.setEvaluateTime(LocalDateTime.now());

        MPJLambdaWrapper<StudentCourse> evaluateWrapper = new MPJLambdaWrapper<>();
        evaluateWrapper.eq(StudentCourse::getStudentId, studentCourseDTO.getStudentId())
                .eq(StudentCourse::getCourseId, studentCourseDTO.getCourseId());
        studentCourseMapper.update(studentCourse, evaluateWrapper);
    }

    // 通过课程获取所有未退选的学生
    @Override
    public List<Integer> getStudentIdsByCourseId(Integer id) {
        return studentCourseMapper.selectStudentIdsByCourseId(id);
    }
}