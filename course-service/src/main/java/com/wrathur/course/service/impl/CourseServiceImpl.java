package com.wrathur.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseDTO;
import com.wrathur.course.domain.po.Course;
import com.wrathur.course.domain.po.StudentCourse;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.mapper.CourseMapper;
import com.wrathur.course.mapper.StudentCourseMapper;
import com.wrathur.course.service.ICourseService;
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
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;

    public CourseServiceImpl(CourseMapper courseMapper, StudentCourseMapper studentCourseMapper) {
        this.courseMapper = courseMapper;
        this.studentCourseMapper = studentCourseMapper;
    }

    // 创建课程
    @Override
    @Transactional
    public void createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
    }

    // 修改课程
    @Override
    @Transactional
    public void modifyCourse(Integer id, CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<Course> modifyWrapper = new UpdateWrapper<>();
        modifyWrapper.eq("id", id);
        courseMapper.update(course, modifyWrapper);
    }

    // 删除课程
    @Override
    public void deleteCourse(Integer id) {
        courseMapper.deleteById(id);
    }

    // 获取课程详情
    @Override
    public CourseVO getCourseDetail(Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);

        //特殊属性需要额外赋值
        vo.setCreateTime(course.getCreateTime());
        vo.setUpdateTime(course.getUpdateTime());
        return vo;
    }

    // 获取课程列表
    @Override
    public IPage<CourseVO> getAllCourses(CourseQueryDTO courseQueryDTO) {
        // 构建分页对象
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

        // 模糊查询课程名称
        if (courseQueryDTO.getName() != null && !courseQueryDTO.getName().isEmpty()) {
            queryWrapper.like(Course::getName, courseQueryDTO.getName());
        }
        // 精确查询学科
        if (courseQueryDTO.getSubjectId() != null) {
            queryWrapper.eq(Course::getSubjectId, courseQueryDTO.getSubjectId());
        }
        // 精确查询类型
        if (courseQueryDTO.getTypeId() != null) {
            queryWrapper.eq(Course::getTypeId, courseQueryDTO.getTypeId());
        }

        // 执行分页查询
        IPage<Course> coursePage = courseMapper.selectPage(page, queryWrapper);

        // 转换为VO
        List<CourseVO> vos = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO vo = new CourseVO();
                    BeanUtils.copyProperties(course, vo);
                    vo.setCreateTime(course.getCreateTime());
                    vo.setUpdateTime(course.getUpdateTime());
                    return vo;
                })
                .collect(Collectors.toList());

        // 构建返回的分页VO
        IPage<CourseVO> resultPage = new Page();
        resultPage.setRecords(vos);
        resultPage.setTotal(coursePage.getTotal());
        resultPage.setPages(coursePage.getPages());
        resultPage.setCurrent(coursePage.getCurrent());
        resultPage.setSize(coursePage.getSize());
        return resultPage;
    }

    // 审核课程
    @Override
    public void reviewCourse(String reviewStatus, CourseDTO courseDTO) {
        Course course = new Course();
        course.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<Course> reviewWrapper = new UpdateWrapper<>();
        reviewWrapper.eq("id", courseDTO.getId()).set("review_status", reviewStatus);
        courseMapper.update(course, reviewWrapper);
    }

    // 选修课程
    @Override
    public void selectCourse(Integer id, CourseDTO courseDTO) {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(id);
        studentCourse.setCourseId(courseDTO.getId());
        studentCourse.setProgress(0);
        studentCourse.setScore(null);
        studentCourse.setCreateTime(LocalDateTime.now());
        studentCourse.setUpdateTime(LocalDateTime.now());

        studentCourseMapper.insert(studentCourse);
    }

    // 评定课程
    @Override
    public void evaluateCourse(BigDecimal score, StudentCourseDTO studentCourseDTO) {
        StudentCourse studentCourse = new StudentCourse();
        BeanUtils.copyProperties(studentCourseDTO, studentCourse);
        studentCourse.setScore(score);
        studentCourse.setUpdateTime(LocalDateTime.now());

        MPJLambdaWrapper<StudentCourse> evaluateWrapper = new MPJLambdaWrapper<>();
        evaluateWrapper.eq(StudentCourse::getStudentId, studentCourseDTO.getStudentId())
                .eq(StudentCourse::getCourseId, studentCourseDTO.getCourseId());
        studentCourseMapper.update(studentCourse, evaluateWrapper);
    }
}