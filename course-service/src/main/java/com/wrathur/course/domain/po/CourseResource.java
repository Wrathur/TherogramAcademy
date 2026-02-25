package com.wrathur.course.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResource {
    private Integer id;
    private String name;
    private String uri;
    private String resourceType; //VIDEO/MATERIAL/REFERENCE
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}