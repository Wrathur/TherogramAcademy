package com.wrathur.user.service;

import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.vo.UserVO;

public interface IUserService {
    // 创建用户
    public void createUser(UserDTO userDTO);

    // 修改用户
    public void modifyUser(UserDTO userDTO);

    // 删除用户
    public void deleteUser(UserDTO userDTO);

    // 获取用户详情
    public UserVO getUserDetail(Integer userId);
}