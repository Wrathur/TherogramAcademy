package com.wrathur.course.domain.vo;

import lombok.Data;

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
    private Integer subjectId;
    private Integer typeId;
    private String rejectedReason;
    private String createTime;
    private String updateTime;
    private String reviewTime;
    private String deleteTime;

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}