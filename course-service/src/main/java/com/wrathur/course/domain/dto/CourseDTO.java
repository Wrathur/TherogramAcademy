package com.wrathur.course.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDTO {
    private Integer id;
    private String name;
    private String profile;
    private String target;
    private String content;
    private String outline;
    private Integer reviewStatus;
    private Integer subjectId;
    private Integer typeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}