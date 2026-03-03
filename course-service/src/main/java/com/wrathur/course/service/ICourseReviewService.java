package com.wrathur.course.service;

import com.wrathur.course.domain.dto.CourseDTO;

public interface ICourseReviewService {
    // 审核课程
    public Integer reviewCourse(CourseDTO courseDTO);
}