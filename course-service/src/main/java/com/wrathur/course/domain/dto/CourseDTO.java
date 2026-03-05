package com.wrathur.course.domain.dto;

import com.wrathur.course.domain.po.Course;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDTO{
    private Integer id;
    private String name;
    private String profile;
    private String target;
    private String content;
    private String outline;
    private String reviewStatus;
    private String rejectedReason;
    private Boolean isDeleted;
    private Integer subjectId;
    private Integer typeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime reviewTime;
    private LocalDateTime deleteTime;
}