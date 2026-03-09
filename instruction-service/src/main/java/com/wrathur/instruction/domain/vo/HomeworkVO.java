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
    private Integer submitCount;
    private Boolean isDeleted;
    private String createTime;
    private String updateTime;
    private String deleteTime;

    // 关联表字段
    private String reviewStatus;
    private String score;
    private String submitTime;

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        if (deleteTime != null) {
            this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public void setScore(BigDecimal score) {
        this.score = score != null ? String.format("%.1f", score) : "PENDING";
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        if (submitTime != null) {
            this.submitTime = submitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}