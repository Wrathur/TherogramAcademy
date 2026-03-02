package com.wrathur.user.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String account;
    private String roleType;
    private String profile;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}