package com.wrathur.user.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class UserVO {
    private Integer id;
    private String account;
    private String username;
    private String roleType;
    private String profile;
    private String createTime;
    private String updateTime;
    private String deleteTime;

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}