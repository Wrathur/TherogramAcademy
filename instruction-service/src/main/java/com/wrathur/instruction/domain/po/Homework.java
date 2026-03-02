package com.wrathur.instruction.domain.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Homework {
    private Integer id;
    private String name;
    private LocalDateTime deadline;
    private String content;
    private String attachment;
    private Boolean reviewStatus; //ENUM: PENDING/APPROVED/REJECTED
    private BigDecimal score;
    private Integer courseId;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}