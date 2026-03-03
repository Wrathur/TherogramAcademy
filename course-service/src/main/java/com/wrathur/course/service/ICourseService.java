package com.wrathur.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.vo.CourseVO;

import java.util.List;

public interface ICourseService {
    // 创建课程
    public void createCourse(CourseDTO courseDTO);

    // 修改课程
    public void modifyCourse(CourseDTO courseDTO);

    // 删除课程
    public void deleteCourse(Integer id);

    // 获取课程列表
    public IPage<CourseVO> getAllCourses(CourseQueryDTO courseQueryDTO);

    // 获取课程详情
    public CourseVO getCourseDetail(Integer courseId);

    // 评定课程
    public void evaluateCourse(CourseDTO courseDTO);
}