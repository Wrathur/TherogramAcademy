package com.wrathur.instruction.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class StudentHomeworkVO {
    private Integer studentId;
    private Integer homeworkId;
    private String attachment;
    private String reviewStatus;
    private String score;
    private String rejectedReason;
    private String createTime;
    private String updateTime;
    private String submitTime;
    private String evaluateTime;
    private String deleteTime;

    public void setScore(BigDecimal score) {
        this.score = score != null ? String.format("%.1f", score) : "PENDING";
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setEvaluateTime(LocalDateTime evaluateTime) {
        this.evaluateTime = evaluateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}