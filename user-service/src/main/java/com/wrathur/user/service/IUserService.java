package com.wrathur.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.po.User;
import com.wrathur.user.domain.vo.UserVO;

public interface IUserService extends IService<User> {
    // 创建用户
    public void createUser(UserDTO userDTO);

    // 登录用户
    public UserVO loginUser(UserDTO userDTO);

    // 修改用户
    public void modifyUser(UserDTO userDTO);

    // 删除用户
    public void deleteUser();

    // 获取用户分页
    public IPage<UserVO> getUserPages(UserQueryDTO userQueryDTO);

    // 获取用户详情
    public UserVO getUserDetail();

    // 通过ID获取用户名
    public String getUsernameById(Integer id);
}