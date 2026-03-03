package com.wrathur.instruction.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class HomeworkVO {
    private Integer id;
    private String name;
    private String type;
    private String deadline;
    private String content;
    private String attachment;
    private String reviewStatus;
    private String score;

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setScore(BigDecimal score) {
        this.score = score != null ? String.format("%.1f", score) : "PENDING";
    }
}