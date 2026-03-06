package com.wrathur.course.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
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
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.po.Course;
import com.wrathur.course.domain.po.Homework;
import com.wrathur.course.domain.po.StudentCourse;
import com.wrathur.course.domain.po.StudentHomework;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.domain.vo.StudentCourseVO;
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
        course.setSelectCount(0);
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
    public IPage<CourseVO> getCoursePages(Integer id, CourseQueryDTO courseQueryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Course> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤未过审课程
        pageWrapper.eq(Course::getReviewStatus, "APPROVED");
        // 过滤已删除课程
        pageWrapper.eq(Course::getIsDeleted, false);

        // 模糊查询名称
        if (courseQueryDTO.getName() != null && StringUtils.isNotBlank(courseQueryDTO.getName())) {
            pageWrapper.like(Course::getName, "%" + courseQueryDTO.getName() + "%");
        }
        // 精确查询学科
        if (courseQueryDTO.getSubjectId() != null) {
            pageWrapper.eq(Course::getSubjectId, courseQueryDTO.getSubjectId());
        }
        // 精确查询类型
        if (courseQueryDTO.getTypeId() != null) {
            pageWrapper.eq(Course::getTypeId, courseQueryDTO.getTypeId());
        }

        // 范围查询选课人数
        if (courseQueryDTO.getStartSelectCount() != null) {
            pageWrapper.ge(Course::getSelectCount, courseQueryDTO.getStartSelectCount());
        }
        if (courseQueryDTO.getEndSelectCount() != null) {
            pageWrapper.le(Course::getSelectCount, courseQueryDTO.getEndSelectCount());
        }
        // 范围查询创建时间
        if (courseQueryDTO.getStartCreateTime() != null) {
            pageWrapper.ge(Course::getCreateTime, courseQueryDTO.getStartCreateTime());
        }
        if (courseQueryDTO.getEndCreateTime() != null) {
            pageWrapper.le(Course::getCreateTime, courseQueryDTO.getEndCreateTime());
        }

        // 筛选后的课程ID列表
        List<Object> courseIdsObj = courseMapper.selectObjs(pageWrapper);
        List<Integer> courseIds = courseIdsObj.stream()
                .map(obj -> (Integer) obj)
                .collect(Collectors.toList());

        // 过滤已选修课程
        if (courseQueryDTO.getIsSelected() != null && courseQueryDTO.getIsSelected()) {
            // 获取该学生已选修的课程ID
            LambdaQueryWrapper<StudentCourse> studentCoursePageWrapper = new LambdaQueryWrapper<>();
            List<Object> selectCourseIdsObj = studentCourseMapper.selectObjs(studentCoursePageWrapper
                    .select(StudentCourse::getCourseId)
                    .eq(StudentCourse::getStudentId, id)
                    .in(StudentCourse::getCourseId, courseIds));
            List<Integer> selectCourseIds = selectCourseIdsObj.stream()
                    .map(obj -> (Integer) obj)
                    .toList();

            // 从课程ID列表中过滤已选修的课程
            courseIds.removeAll(selectCourseIds);
        }

        // 如果没有符合条件的课程
        if (courseIds.isEmpty()) {
            return new Page<>();
        }

        pageWrapper.in(Course::getId, courseIds);
        // 按ID排序
        pageWrapper.orderByDesc(Course::getId);
        // 按选课人数排序
        if (courseQueryDTO.getSelectCountAsc() != null) {
            if (courseQueryDTO.getSelectCountAsc()) {
                pageWrapper.orderByAsc(Course::getSelectCount);
            } else {
                pageWrapper.orderByDesc(Course::getSelectCount);
            }
        }
        // 按创建时间排序
        if (courseQueryDTO.getSelectCountAsc() != null) {
            if (courseQueryDTO.getSelectCountAsc()) {
                pageWrapper.orderByAsc(Course::getCreateTime);
            } else {
                pageWrapper.orderByDesc(Course::getCreateTime);
            }
        }

        // 构建分页对象
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());

        // 执行分页查询
        IPage<Course> coursePage = courseMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<CourseVO> courseVOS = coursePage.getRecords().stream()
                .map(this::convertCourseToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(coursePage, courseVOS);
    }

    // 获取创建课程分页
    @Override
    public IPage<CourseVO> getCreateCoursePages(Integer id, CourseQueryDTO courseQueryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Course> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤非该教师创建的课程
        pageWrapper.eq(Course::getTeacherId, id);

        // 模糊查询名称
        if (courseQueryDTO.getName() != null && StringUtils.isNotBlank(courseQueryDTO.getName())) {
            pageWrapper.like(Course::getName, "%" + courseQueryDTO.getName() + "%");
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
        // 精确查询删除状态
        if (courseQueryDTO.getIsDeleted() != null && courseQueryDTO.getIsDeleted()) {
            pageWrapper.eq(Course::getIsDeleted, false);
        }

        // 范围查询选课人数
        if (courseQueryDTO.getStartSelectCount() != null) {
            pageWrapper.ge(Course::getSelectCount, courseQueryDTO.getStartSelectCount());
        }
        if (courseQueryDTO.getEndSelectCount() != null) {
            pageWrapper.le(Course::getSelectCount, courseQueryDTO.getEndSelectCount());
        }
        // 范围查询创建时间
        if (courseQueryDTO.getStartCreateTime() != null) {
            pageWrapper.ge(Course::getCreateTime, courseQueryDTO.getStartCreateTime());
        }
        if (courseQueryDTO.getEndCreateTime() != null) {
            pageWrapper.le(Course::getCreateTime, courseQueryDTO.getEndCreateTime());
        }

        // 按ID排序
        pageWrapper.orderByDesc(Course::getId);
        // 按选课人数排序
        if (courseQueryDTO.getSelectCountAsc() != null) {
            if (courseQueryDTO.getSelectCountAsc()) {
                pageWrapper.orderByAsc(Course::getSelectCount);
            } else {
                pageWrapper.orderByDesc(Course::getSelectCount);
            }
        }
        // 按创建时间排序
        if (courseQueryDTO.getCreateTimeAsc() != null) {
            if (courseQueryDTO.getCreateTimeAsc()) {
                pageWrapper.orderByAsc(Course::getCreateTime);
            } else {
                pageWrapper.orderByDesc(Course::getCreateTime);
            }
        }

        // 构建分页对象
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());

        // 执行分页查询
        IPage<Course> coursePage = courseMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<CourseVO> courseVOS = coursePage.getRecords().stream()
                .map(this::convertCourseToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(coursePage, courseVOS);
    }

    // 获取选修课程分页
    @Override
    public IPage<CourseVO> getSelectCoursePages(Integer id, StudentCourseQueryDTO studentCourseQueryDTO) {
        // 构建查询条件，从学生课程关联表获取已选修课程ID列表
        LambdaQueryWrapper<StudentCourse> studentCoursePageWrapper = new LambdaQueryWrapper<>();
        studentCoursePageWrapper.eq(StudentCourse::getStudentId, id);

        // 范围查询进度（关联表字段）
        if (studentCourseQueryDTO.getStartProgress() != null) {
            studentCoursePageWrapper.ge(StudentCourse::getProgress, studentCourseQueryDTO.getStartProgress());
        }
        if (studentCourseQueryDTO.getEndProgress() != null) {
            studentCoursePageWrapper.le(StudentCourse::getProgress, studentCourseQueryDTO.getEndProgress());
        }
        // 范围查询学习时间（关联表字段）
        if (studentCourseQueryDTO.getStartStudyTime() != null) {
            studentCoursePageWrapper.ge(StudentCourse::getStudyTime, studentCourseQueryDTO.getStartStudyTime());
        }
        if (studentCourseQueryDTO.getEndStudyTime() != null) {
            studentCoursePageWrapper.le(StudentCourse::getStudyTime, studentCourseQueryDTO.getEndStudyTime());
        }
        // 范围查询分数（关联表字段）
        if (studentCourseQueryDTO.getStartScore() != null) {
            studentCoursePageWrapper.ge(StudentCourse::getScore, studentCourseQueryDTO.getStartScore());
        }
        if (studentCourseQueryDTO.getEndScore() != null) {
            studentCoursePageWrapper.le(StudentCourse::getScore, studentCourseQueryDTO.getEndScore());
        }
        // 范围查询选修时间（关联表字段）
        if (studentCourseQueryDTO.getStartSelectTime() != null) {
            studentCoursePageWrapper.ge(StudentCourse::getSelectTime, studentCourseQueryDTO.getStartSelectTime());
        }
        if (studentCourseQueryDTO.getEndSelectTime() != null) {
            studentCoursePageWrapper.le(StudentCourse::getSelectTime, studentCourseQueryDTO.getEndSelectTime());
        }

        // 按进度排序
        if (studentCourseQueryDTO.getProgressAsc() != null) {
            if (studentCourseQueryDTO.getProgressAsc()) {
                studentCoursePageWrapper.orderByAsc(StudentCourse::getProgress);
            } else {
                studentCoursePageWrapper.orderByDesc(StudentCourse::getProgress);
            }
        }
        // 按学习时间排序
        if (studentCourseQueryDTO.getStudyTimeAsc() != null) {
            if (studentCourseQueryDTO.getStudyTimeAsc()) {
                studentCoursePageWrapper.orderByAsc(StudentCourse::getStudyTime);
            } else {
                studentCoursePageWrapper.orderByDesc(StudentCourse::getStudyTime);
            }
        }
        // 按分数排序
        if (studentCourseQueryDTO.getScoreAsc() != null) {
            if (studentCourseQueryDTO.getScoreAsc()) {
                studentCoursePageWrapper.orderByAsc(StudentCourse::getScore);
            } else {
                studentCoursePageWrapper.orderByDesc(StudentCourse::getScore);
            }
        }
        // 按选课时间排序
        if (studentCourseQueryDTO.getSelectTimeAsc() != null) {
            if (studentCourseQueryDTO.getSelectTimeAsc()) {
                studentCoursePageWrapper.orderByAsc(StudentCourse::getSelectTime);
            } else {
                studentCoursePageWrapper.orderByDesc(StudentCourse::getSelectTime);
            }
        }

        List<Object> courseIds = studentCourseMapper.selectObjs(studentCoursePageWrapper);

        // 如果没有符合条件的课程
        if (courseIds == null || courseIds.isEmpty()) {
            return new Page<>();
        }

        // 从课程表查询符合条件的课程
        LambdaQueryWrapper<Course> coursePageWrapper = new LambdaQueryWrapper<>();
        coursePageWrapper.in(Course::getId, courseIds);

        // 过滤已删除课程（课程表字段）
        coursePageWrapper.eq(Course::getIsDeleted, false);

        // 模糊查询名称（课程表字段）
        if (studentCourseQueryDTO.getName() != null && StringUtils.isNotBlank(studentCourseQueryDTO.getName())) {
            coursePageWrapper.like(Course::getName, "%" + studentCourseQueryDTO.getName() + "%");
        }
        // 精确查询学科（课程表字段）
        if (studentCourseQueryDTO.getSubjectId() != null) {
            coursePageWrapper.eq(Course::getSubjectId, studentCourseQueryDTO.getSubjectId());
        }
        // 精确查询类型（课程表字段）
        if (studentCourseQueryDTO.getTypeId() != null) {
            coursePageWrapper.eq(Course::getTypeId, studentCourseQueryDTO.getTypeId());
        }

        // 范围查询选课人数（课程表字段）
        if (studentCourseQueryDTO.getStartSelectCount() != null) {
            coursePageWrapper.ge(Course::getSelectCount, studentCourseQueryDTO.getStartSelectCount());
        }
        if (studentCourseQueryDTO.getEndSelectCount() != null) {
            coursePageWrapper.le(Course::getSelectCount, studentCourseQueryDTO.getEndSelectCount());
        }
        // 范围查询创建时间（课程表字段）
        if (studentCourseQueryDTO.getStartCreateTime() != null) {
            coursePageWrapper.ge(Course::getCreateTime, studentCourseQueryDTO.getStartCreateTime());
        }
        if (studentCourseQueryDTO.getEndCreateTime() != null) {
            coursePageWrapper.le(Course::getCreateTime, studentCourseQueryDTO.getEndCreateTime());
        }

        // 按ID排序（课程表字段）
        coursePageWrapper.orderByDesc(Course::getId);
        // 按选课人数排序（课程表字段）
        if (studentCourseQueryDTO.getSelectCountAsc() != null) {
            if (studentCourseQueryDTO.getSelectCountAsc()) {
                coursePageWrapper.orderByAsc(Course::getSelectCount);
            } else {
                coursePageWrapper.orderByDesc(Course::getSelectCount);
            }
        }
        // 按创建时间排序（课程表字段）
        if (studentCourseQueryDTO.getSelectCountAsc() != null) {
            if (studentCourseQueryDTO.getSelectCountAsc()) {
                coursePageWrapper.orderByAsc(Course::getCreateTime);
            } else {
                coursePageWrapper.orderByDesc(Course::getCreateTime);
            }
        }

        // 构建分页对象
        Page<Course> page = new Page<>(studentCourseQueryDTO.getPageNum(), studentCourseQueryDTO.getPageSize());

        // 执行分页查询
        IPage<Course> coursePage = courseMapper.selectPage(page, coursePageWrapper);

        // 转换为VO
        List<CourseVO> courseVOS = coursePage.getRecords().stream()
                .map(this::convertCourseToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(coursePage, courseVOS);
    }

    //转化VO
    private CourseVO convertCourseToVO(Course course) {
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        courseVO.setCreateTime(course.getCreateTime());
        courseVO.setUpdateTime(course.getUpdateTime());
        courseVO.setReviewTime(course.getReviewTime());
        courseVO.setDeleteTime(course.getDeleteTime());
        return courseVO;
    }

    //转化分页结果
    private IPage<CourseVO> convertPageResult(IPage<Course> coursePage, List<CourseVO> courseVOS) {
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
        courseVO.setTeacherId(course.getTeacherId());
        courseVO.setCreateTime(course.getCreateTime());
        courseVO.setUpdateTime(course.getUpdateTime());
        courseVO.setReviewTime(course.getReviewTime());
        courseVO.setDeleteTime(course.getDeleteTime());
        return courseVO;
    }

    @Override
    public CourseVO getCreateCourseDetail(Integer id) {
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

    @Override
    public StudentCourseVO getSelectCourseDetail(Integer studentId, Integer courseId) {
        LambdaQueryWrapper<StudentCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentCourse::getStudentId, studentId)
                .eq(StudentCourse::getCourseId, courseId)
                .eq(StudentCourse::getIsDeleted, false);
        StudentCourse studentCourse = studentCourseMapper.selectOne(queryWrapper);
        StudentCourseVO studentCourseVO = new StudentCourseVO();
        BeanUtils.copyProperties(studentCourse, studentCourseVO);

        // 特殊属性需要额外赋值
        studentCourseVO.setProgress(studentCourse.getProgress());
        studentCourseVO.setStudyTime(studentCourse.getStudyTime());
        studentCourseVO.setScore(studentCourse.getScore());
        studentCourseVO.setCreateTime(studentCourse.getCreateTime());
        studentCourseVO.setUpdateTime(studentCourse.getUpdateTime());
        studentCourseVO.setSelectTime(studentCourse.getSelectTime());
        studentCourseVO.setEvaluateTime(studentCourse.getEvaluateTime());
        studentCourseVO.setDeleteTime(studentCourse.getDeleteTime());
        return studentCourseVO;
    }

    // 审核课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewCourse(String reviewStatus, CourseDTO courseDTO) {
        Course course = courseMapper.selectById(courseDTO.getId());
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
        studentCourse.setStudyTime(0);
        studentCourse.setCreateTime(LocalDateTime.now());
        studentCourse.setUpdateTime(LocalDateTime.now());
        studentCourse.setSelectTime(LocalDateTime.now());
        studentCourseMapper.insert(studentCourse);

        // 选课人数增加
        Course course = courseMapper.selectById(courseId);
        course.setSelectCount(course.getSelectCount() + 1);
        UpdateWrapper<Course> selectWrapper = new UpdateWrapper<>();
        selectWrapper.eq("id", courseId);
        courseMapper.update(course, selectWrapper);

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

        // 选课人数减少
        Course course = courseMapper.selectById(courseId);
        course.setSelectCount(course.getSelectCount() - 1);
        UpdateWrapper<Course> selectWrapper = new UpdateWrapper<>();
        selectWrapper.eq("id", courseId);
        courseMapper.update(course, selectWrapper);

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