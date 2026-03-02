package com.wrathur.instruction.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {
    private Integer id;
    private String name;
    private String profile;
    private String target;
    private String content;
    private String outline;
    private Integer reviewStatus; //ENUM: PENDING/APPROVED/REJECTED
    private Integer subjectId;
    private Integer typeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}