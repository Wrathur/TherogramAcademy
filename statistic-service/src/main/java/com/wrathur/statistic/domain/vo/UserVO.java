package com.wrathur.statistic.domain.vo;

import lombok.Data;

@Data
public class UserVO {
    private Integer id;
    private String account;
    private String username;
    private String roleType;
    private String profile;
}