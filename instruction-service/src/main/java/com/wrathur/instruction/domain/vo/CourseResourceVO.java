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
    private String courseId;
    private String createTime;
    private String updateTime;

    public void setResourceTypeDesc(String resourceType) {
        this.resourceType = switch (resourceType) {
            case "VIDEO" -> "视频";
            case "MATERIAL" -> "课件";
            case "REFERENCE" -> "参考资料";
            default -> "其他";
        };
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}