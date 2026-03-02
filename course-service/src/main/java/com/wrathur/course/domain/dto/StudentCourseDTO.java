package com.wrathur.course.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentCourseDTO {
    private Integer studentId;
    private Integer courseId;
    private Integer progress;
    private BigDecimal score;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}