package com.wrathur.statistic.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String account;
    private String username;
    private String password;
    private String roleType;
    private String profile;
    @TableField("is_deleted")
    private Boolean idDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}