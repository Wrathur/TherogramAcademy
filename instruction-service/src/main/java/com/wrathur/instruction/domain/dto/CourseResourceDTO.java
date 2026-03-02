package com.wrathur.instruction.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResourceDTO {
    private Integer id;
    private String name;
    private String uri;
    private String resourceType; // VIDEO/MATERIAL/REFERENCE
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}