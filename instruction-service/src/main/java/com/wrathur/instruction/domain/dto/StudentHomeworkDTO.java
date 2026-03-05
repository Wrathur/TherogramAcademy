package com.wrathur.instruction.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentHomeworkDTO {
    private Integer studentId;
    private Integer homeworkId;
    private String attachment;
    private String reviewStatus;
    private BigDecimal score;
    private String rejectedReason;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime submitTime;
    private LocalDateTime evaluateTime;
    private LocalDateTime deleteTime;
}