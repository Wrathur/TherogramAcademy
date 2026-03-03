package com.wrathur.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseDTO;
import com.wrathur.course.domain.vo.CourseVO;

import java.math.BigDecimal;

public interface ICourseService {
    // 创建课程
    public void createCourse(CourseDTO courseDTO);

    // 修改课程
    public void modifyCourse(Integer id, CourseDTO courseDTO);

    // 删除课程
    public void deleteCourse(Integer id);

    // 获取课程列表
    public IPage<CourseVO> getAllCourses(CourseQueryDTO courseQueryDTO);

    // 获取课程详情
    public CourseVO getCourseDetail(Integer courseId);

    // 审核课程
    public void reviewCourse(String reviewStatus, CourseDTO courseDTO);

    // 选修课程
    public void selectCourse(Integer id, CourseDTO courseDTO);

    // 评定课程
    public void evaluateCourse(BigDecimal score, StudentCourseDTO studentCourseDTO);
}