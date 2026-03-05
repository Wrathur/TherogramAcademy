package com.wrathur.course.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class StudentCourseVO {
    private Integer studentId;
    private Integer courseId;
    private String progress;
    private String score;
    private String createTime;
    private String updateTime;
    private String selectTime;
    private String evaluateTime;
    private String deleteTime;

    public void setProgress(Integer progress) {
        this.progress = progress != null ? progress + "%" : "0%";
    }

    public void setScore(Double score) {
        this.score = score != null ? String.format("%.1f", score) : "PENDING";
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setSelectTime(LocalDateTime selectTime) {
        this.selectTime = selectTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setEvaluateTime(LocalDateTime evaluateTime) {
        this.evaluateTime = evaluateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}