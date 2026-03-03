package com.wrathur.instruction.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HomeworkDTO {
    private Integer id;
    private String name;
    private Integer type;
    private String deadline;
    private String content;
    private String attachment;
    private String reviewStatus;
    private BigDecimal score;
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}