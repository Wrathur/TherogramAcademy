package com.wrathur.course.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wrathur.api.client.InstructionServiceClient;
import com.wrathur.api.client.UserServiceClient;
import com.wrathur.common.utils.UserContext;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.po.*;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.domain.vo.StudentCourseVO;
import com.wrathur.course.mapper.*;
import com.wrathur.course.service.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements ICourseService {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;
    private final CourseResourceMapper courseResourceMapper;
    private final HomeworkMapper homeworkMapper;
    private final StudentHomeworkMapper studentHomeworkMapper;
    private final InstructionServiceClient instructionServiceClient;
    private final UserServiceClient userServiceClient;

    // 创建课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setReviewStatus("PENDING");
        course.setSelectCount(0);
        course.setIsDeleted(false);
        System.out.println(UserContext.getUser());
        course.setTeacherId(UserContext.getUser());
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
        courseMapper.update(course,
                new LambdaUpdateWrapper<Course>()
                        .eq(Course::getId, id));
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

        // 远程调用教学服务获取该课程所有未删除的教学资源
        List<Integer> courseResourceIds = instructionServiceClient.getCourseResourceIdsByCourseId(id);

        // 查询该课程的所有教学资源，将其从教学资源表逻辑删除
        if (courseResourceIds != null && !courseResourceIds.isEmpty()) {
            // 删除作业表项
            courseResourceMapper.update(null,
                    new LambdaUpdateWrapper<CourseResource>()
                            .in(CourseResource::getId, courseResourceIds)
                            .set(CourseResource::getIsDeleted, true)
                            .set(CourseResource::getDeleteTime, LocalDateTime.now()));
        }

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

        // 如果没有符合条件的课程
        if (courseIds.isEmpty()) {
            return new Page<>();
        }

        // 过滤已选修课程
        if (courseQueryDTO.getIsSelected() != null && courseQueryDTO.getIsSelected()) {
            // 获取该学生已选修的课程ID
            List<Object> selectCourseIdsObj = studentCourseMapper.selectObjs(
                    new LambdaQueryWrapper<StudentCourse>()
                            .select(StudentCourse::getCourseId)
                            .eq(StudentCourse::getStudentId, UserContext.getUser())
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

        // 排序
        if (courseQueryDTO.getSortType() != null && courseQueryDTO.getIsAsc() != null) {
            if (courseQueryDTO.getIsAsc()) {
                switch (courseQueryDTO.getSortType()) {
                    case 0:
                        pageWrapper.orderByAsc(Course::getSelectCount);
                        break;
                    case 1:
                        pageWrapper.orderByAsc(Course::getCreateTime);
                        break;
                    default:
                        pageWrapper.orderByAsc(Course::getId);
                        break;
                }
            } else {
                switch (courseQueryDTO.getSortType()) {
                    case 0:
                        pageWrapper.orderByDesc(Course::getSelectCount);
                        break;
                    case 1:
                        pageWrapper.orderByDesc(Course::getCreateTime);
                        break;
                    default:
                        pageWrapper.orderByDesc(Course::getId);
                        break;
                }
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
    public IPage<CourseVO> getCreateCoursePages(CourseQueryDTO courseQueryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Course> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤非该教师创建的课程
        pageWrapper.eq(Course::getTeacherId, UserContext.getUser());

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

        // 排序
        if (courseQueryDTO.getSortType() != null && courseQueryDTO.getIsAsc() != null) {
            if (courseQueryDTO.getIsAsc()) {
                switch (courseQueryDTO.getSortType()) {
                    case 0:
                        pageWrapper.orderByAsc(Course::getSelectCount);
                        break;
                    case 1:
                        pageWrapper.orderByAsc(Course::getCreateTime);
                        break;
                    default:
                        pageWrapper.orderByAsc(Course::getId);
                        break;
                }
            } else {
                switch (courseQueryDTO.getSortType()) {
                    case 0:
                        pageWrapper.orderByDesc(Course::getSelectCount);
                        break;
                    case 1:
                        pageWrapper.orderByDesc(Course::getCreateTime);
                        break;
                    default:
                        pageWrapper.orderByDesc(Course::getId);
                        break;
                }
            }
        }

        // 构建分页对象
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());

        // 执行分页查询
        IPage<Course> createCoursePage = courseMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<CourseVO> courseVOS = createCoursePage.getRecords().stream()
                .map(this::convertCourseToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(createCoursePage, courseVOS);
    }

    // 获取选修课程分页
    @Override
    public IPage<CourseVO> getSelectCoursePages(StudentCourseQueryDTO studentCourseQueryDTO) {
        // 构建分页对象
        Page<CourseVO> page = new Page<>(studentCourseQueryDTO.getPageNum(), studentCourseQueryDTO.getPageSize());

        // 执行分页查询
        IPage<CourseVO> selectCoursePage = courseMapper.selectStudentCoursePage(page, UserContext.getUser(), studentCourseQueryDTO);

        // 转换为VO
        selectCoursePage.getRecords().forEach(courseVO -> {
            courseVO.setTeacherId(userServiceClient.getUsernameById(Integer.valueOf(courseVO.getTeacherId())));
            StudentCourse studentCourse = studentCourseMapper.selectOne(
                    new LambdaQueryWrapper<StudentCourse>()
                            .eq(StudentCourse::getStudentId, UserContext.getUser())
                            .eq(StudentCourse::getCourseId, courseVO.getId()));
            courseVO.setProgress(studentCourse.getProgress());
            courseVO.setStudyTime(studentCourse.getStudyTime());
            courseVO.setScore(studentCourse.getScore());
            courseVO.setStudentCourseCreateTime(studentCourse.getCreateTime());
            courseVO.setStudentCourseUpdateTime(studentCourse.getUpdateTime());
            courseVO.setStudentCourseSelectTime(studentCourse.getSelectTime());
            courseVO.setStudentCourseEvaluateTime(studentCourse.getEvaluateTime());
        });

        // 构建返回的分页VO
        return selectCoursePage;
    }

    //转化VO
    private CourseVO convertCourseToVO(Course course) {
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        courseVO.setTeacherId(userServiceClient.getUsernameById(course.getTeacherId()));
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
        courseVO.setTeacherId(userServiceClient.getUsernameById(course.getTeacherId()));
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
    public StudentCourseVO getSelectCourseDetail(Integer id) {
        StudentCourse studentCourse = studentCourseMapper.selectOne(
                new LambdaQueryWrapper<StudentCourse>().eq(StudentCourse::getStudentId, UserContext.getUser())
                        .eq(StudentCourse::getCourseId, id)
                        .eq(StudentCourse::getIsDeleted, false));

        if (studentCourse == null) {
            return null;
        }

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
        // 构建更新条件
        LambdaUpdateWrapper<Course> reviewWrapper = new LambdaUpdateWrapper<>();
        reviewWrapper.eq(Course::getId, courseDTO.getId());

        // 设置审核状态和时间
        reviewWrapper.set(Course::getReviewStatus, reviewStatus);
        reviewWrapper.set(Course::getReviewTime, LocalDateTime.now());

        // 设置拒绝原因（审核通过时为 null）
        if ("REJECTED".equals(reviewStatus)) {
            reviewWrapper.set(Course::getRejectedReason, courseDTO.getRejectedReason());
        } else {
            reviewWrapper.set(Course::getRejectedReason, null);
        }

        // 执行更新
        courseMapper.update(null, reviewWrapper);
    }

    // 选修课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectCourse(Integer id) {
        // 处理学生课程关联表
        StudentCourse existingCourse = studentCourseMapper.selectOne(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, UserContext.getUser())
                        .eq(StudentCourse::getCourseId, id)
                        .eq(StudentCourse::getIsDeleted, false)
        );

        // 如果已存在未删除记录，直接返回
        if (existingCourse != null) {
            return;
        }

        // 检查是否存在已删除的记录
        StudentCourse deletedCourse = studentCourseMapper.selectOne(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, UserContext.getUser())
                        .eq(StudentCourse::getCourseId, id)
                        .eq(StudentCourse::getIsDeleted, true)
        );

        if (deletedCourse != null) {
            // 恢复已删除的课程记录
            deletedCourse.setIsDeleted(false);
            deletedCourse.setUpdateTime(LocalDateTime.now());
            deletedCourse.setSelectTime(LocalDateTime.now());
            // 由于是复合主键，不能用updateById，应使用 update
            studentCourseMapper.update(deletedCourse,
                    new LambdaUpdateWrapper<StudentCourse>()
                            .eq(StudentCourse::getStudentId, UserContext.getUser())
                            .eq(StudentCourse::getCourseId, id));
        } else {
            // 新增课程记录
            StudentCourse studentCourse = new StudentCourse();
            studentCourse.setStudentId(UserContext.getUser());
            studentCourse.setCourseId(id);
            studentCourse.setProgress(0);
            studentCourse.setStudyTime(0);
            studentCourse.setCreateTime(LocalDateTime.now());
            studentCourse.setUpdateTime(LocalDateTime.now());
            studentCourse.setSelectTime(LocalDateTime.now());
            studentCourseMapper.insert(studentCourse);
        }

        // 更新课程选课人数
        Course course = courseMapper.selectById(id);
        course.setSelectCount(course.getSelectCount() + 1);
        courseMapper.updateById(course);

        // 处理学生作业关联表
        List<Integer> homeworkIds = instructionServiceClient.getHomeworkIdsByCourseId(id);
        if (homeworkIds != null && !homeworkIds.isEmpty()) {
            // 批量处理作业关联表
            List<StudentHomework> homeworkList = homeworkIds.stream()
                    .map(homeworkId -> {
                        // 检查该作业是否存在
                        StudentHomework existingHomework = studentHomeworkMapper.selectOne(
                                new LambdaQueryWrapper<StudentHomework>()
                                        .eq(StudentHomework::getStudentId, UserContext.getUser())
                                        .eq(StudentHomework::getHomeworkId, homeworkId)
                                        .eq(StudentHomework::getIsDeleted, false)
                        );

                        // 如果已存在未删除记录，直接返回
                        if (existingHomework != null) {
                            return null;
                        }

                        // 检查是否存在已删除的记录
                        StudentHomework deletedHomework = studentHomeworkMapper.selectOne(
                                new LambdaQueryWrapper<StudentHomework>()
                                        .eq(StudentHomework::getStudentId, UserContext.getUser())
                                        .eq(StudentHomework::getHomeworkId, homeworkId)
                                        .eq(StudentHomework::getIsDeleted, true)
                        );

                        if (deletedHomework != null) {
                            // 恢复已删除的作业记录
                            deletedHomework.setIsDeleted(false);
                            deletedHomework.setUpdateTime(LocalDateTime.now());
                            studentHomeworkMapper.update(deletedHomework,
                                    new LambdaUpdateWrapper<StudentHomework>()
                                            .eq(StudentHomework::getStudentId, UserContext.getUser())
                                            .eq(StudentHomework::getHomeworkId, homeworkId)
                                            .eq(StudentHomework::getIsDeleted, true));
                            return null;
                        } else {
                            // 新增作业记录
                            StudentHomework studentHomework = new StudentHomework();
                            studentHomework.setStudentId(UserContext.getUser());
                            studentHomework.setHomeworkId(homeworkId);
                            studentHomework.setReviewStatus("UNSUBMITTED");
                            studentHomework.setIsDeleted(false);
                            studentHomework.setCreateTime(LocalDateTime.now());
                            studentHomework.setUpdateTime(LocalDateTime.now());
                            return studentHomework;
                        }
                    })
                    .filter(Objects::nonNull) // 过滤掉已存在的记录
                    .collect(Collectors.toList());

            // 批量插入/更新
            if (!homeworkList.isEmpty()) {
                studentHomeworkMapper.insertBatch(homeworkList);
            }
        }
    }

    // 退选课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deselectCourse(Integer id) {
        // 查询该学生，将其从学生课程表逻辑删除
        studentCourseMapper.update(null,
                new LambdaUpdateWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, UserContext.getUser())
                        .eq(StudentCourse::getCourseId, id)
                        .set(StudentCourse::getIsDeleted, true)
                        .set(StudentCourse::getDeleteTime, LocalDateTime.now()));

        // 选课人数减少
        Course course = courseMapper.selectById(id);
        course.setSelectCount(course.getSelectCount() - 1);
        courseMapper.update(course,
                new LambdaUpdateWrapper<Course>()
                        .eq(Course::getId, id));

        // 远程调用教学服务获取该课程所有未删除的作业
        List<Integer> homeworkIds = instructionServiceClient.getHomeworkIdsByCourseId(id);

        // 查询该课程的所有作业，将其从学生作业表逻辑删除
        if (homeworkIds != null && !homeworkIds.isEmpty()) {
            // 删除学生作业关联表项
            studentHomeworkMapper.update(null,
                    new LambdaUpdateWrapper<StudentHomework>()
                            .eq(StudentHomework::getStudentId, UserContext.getUser())
                            .in(StudentHomework::getHomeworkId, homeworkIds)
                            .set(StudentHomework::getIsDeleted, true)
                            .set(StudentHomework::getDeleteTime, LocalDateTime.now()));
        }
    }

    // 评定课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateCourse(BigDecimal score, Integer studentId, Integer courseId) {
        StudentCourse studentCourse = studentCourseMapper.selectOne(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, studentId)
                        .eq(StudentCourse::getCourseId, courseId)
                        .eq(StudentCourse::getIsDeleted, false));
        studentCourse.setScore(score);
        studentCourse.setEvaluateTime(LocalDateTime.now());

        studentCourseMapper.update(studentCourse,
                new MPJLambdaWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, studentId)
                        .eq(StudentCourse::getCourseId, courseId));
    }

    // 通过课程获取所有未退选的学生
    @Override
    public List<Integer> getStudentIdsByCourseId(Integer id) {
        return baseMapper.selectObjs(new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getCourseId, id)
                        .eq(StudentCourse::getIsDeleted, false)
                        .select(StudentCourse::getStudentId)).stream()
                .map(obj -> (Integer) obj)
                .collect(Collectors.toList());
    }
}