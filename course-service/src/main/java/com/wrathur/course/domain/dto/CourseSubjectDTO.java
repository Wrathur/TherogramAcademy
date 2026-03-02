package com.wrathur.course.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseSubjectDTO {
    private Integer id;
    private String name;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}