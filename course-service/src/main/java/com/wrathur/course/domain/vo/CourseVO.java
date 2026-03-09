package com.wrathur.course.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CourseVO {
    private Integer id;
    private String name;
    private String profile;
    private String target;
    private String content;
    private String outline;
    private String reviewStatus;
    private Boolean isDeleted;
    private Integer subjectId;
    private Integer typeId;
    private String teacherId;
    private String rejectedReason;
    private Integer selectCount;
    private String createTime;
    private String updateTime;
    private String reviewTime;
    private String deleteTime;

    // 关联表字段
    private String progress;
    private String studyTime;
    private String score;
    private String selectTime;

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        if (reviewTime != null) {
            this.reviewTime = reviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        if (deleteTime != null) {
            this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public void setProgress(Integer progress) {
        if (progress != null) {
            this.progress = progress != 0 ? progress + "%" : "0%";
        }
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

    public void setSelectTime(LocalDateTime selectTime) {
        if (selectTime != null) {
            this.selectTime = selectTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}