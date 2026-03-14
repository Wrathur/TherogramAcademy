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
    private String studentHomeworkAttachment;
    private String reviewStatus;
    private String score;
    private String rejectedReason;
    private String studentHomeworkCreateTime;
    private String studentHomeworkUpdateTime;
    private String studentHomeworkSubmitTime;
    private String studentHomeworkEvaluateTime;

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

    public void setStudentHomeworkCreateTime(LocalDateTime studentHomeworkCreateTime) {
        if (studentHomeworkCreateTime != null) {
            this.studentHomeworkCreateTime = studentHomeworkCreateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public void setStudentHomeworkUpdateTime(LocalDateTime studentHomeworkUpdateTime) {
        if (studentHomeworkUpdateTime != null) {
            this.studentHomeworkUpdateTime = studentHomeworkUpdateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public void setStudentHomeworkSubmitTime(LocalDateTime studentHomeworkSubmitTime) {
        if (studentHomeworkSubmitTime != null) {
            this.studentHomeworkSubmitTime = studentHomeworkSubmitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public void setStudentHomeworkEvaluateTime(LocalDateTime studentHomeworkEvaluateTime) {
        if (studentHomeworkEvaluateTime != null) {
            this.studentHomeworkEvaluateTime = studentHomeworkEvaluateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}