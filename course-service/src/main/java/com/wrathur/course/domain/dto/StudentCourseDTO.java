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
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime selectTime;
    private LocalDateTime evaluateTime;
    private LocalDateTime deleteTime;
}