package com.wrathur.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.vo.UserVO;
import io.swagger.annotations.ApiOperation;

public interface IUserService {
    // 创建用户
    public void createUser(UserDTO userDTO);

    // 修改用户
    public void modifyUser(Integer id, UserDTO userDTO);

    // 删除用户
    public void deleteUser(Integer id);

    // 获取用户分页
    public IPage<UserVO> getUserPages(UserQueryDTO userQueryDTO);

    // 获取用户详情
    public UserVO getUserDetail(Integer id);

    // 通过ID获取用户名
    public String getUsernameById(Integer id);
}