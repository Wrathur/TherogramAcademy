package com.wrathur.course.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class StudentCourseVO {
    private Integer studentId;
    private Integer courseId;
    private String progress;
    private String studyTime;
    private String score;
    private String createTime;
    private String updateTime;
    private String selectTime;
    private String evaluateTime;
    private String deleteTime;

    public void setProgress(Integer progress) {
        this.progress = progress != 0 ? progress + "%" : "0%";
    }

    public void setStudyTime(Integer studyTime) {
        if (studyTime == null || studyTime <= 0) {
            this.studyTime = "0分钟";
        } else {
            int hours = studyTime / 60;
            int minutes = studyTime % 60;

            if (hours == 0) {
                this.studyTime = minutes + "分钟";
            } else if (minutes == 0) {
                this.studyTime = hours + "小时";
            } else {
                // 格式化分钟数为两位数（如5分钟显示为05分钟）
                this.studyTime = hours + "小时" + String.format("%02d", minutes) + "分钟";
            }
        }
    }

    public void setScore(BigDecimal score) {
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