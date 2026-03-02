package com.wrathur.course.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseSubject {
    private Integer id;
    private String name;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}