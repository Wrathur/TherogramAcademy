package com.wrathur.instruction.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CourseResourceVO {
    private Integer id;
    private String name;
    private String uri;
    private String resourceType;
    private Integer viewCount;
    private Integer courseId;
    private String createTime;
    private String updateTime;
    private String deleteTime;

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}