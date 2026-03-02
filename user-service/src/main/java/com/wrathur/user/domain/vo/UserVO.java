package com.wrathur.user.domain.vo;

import lombok.Data;

@Data
public class UserVO {
    private Integer id;
    private String account;
    private String roleType;
    private String profile;

    public void setRoleType(String roleType) {
        this.roleType = switch (roleType) {
            case "ADMIN" -> "管理员";
            case "TEACHER" -> "教师";
            case "STUDENT" -> "学生";
            default -> "未知";
        };
    }
}