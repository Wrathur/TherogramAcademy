package com.wrathur.instruction.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HomeworkDTO {
    private Integer id;
    private String name;
    private String type;
    private LocalDateTime deadline;
    private String content;
    private String attachment;
    private Integer submitCount;
    private Boolean isDeleted;
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}