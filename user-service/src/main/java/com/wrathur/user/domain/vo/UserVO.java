package com.wrathur.user.domain.vo;

import lombok.Data;

@Data
public class UserVO {
    private Integer id;
    private String account;
    private String roleType;
    private String profile;
}