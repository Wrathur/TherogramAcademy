package com.wrathur.course.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseType {
    private Integer id;
    private String name;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}