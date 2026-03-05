package com.wrathur.course.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseTypeVO {
    private Integer id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}