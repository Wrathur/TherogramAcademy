package com.wrathur.user.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String account;
    private String password;
    private Integer roleType;
    private String profile;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}