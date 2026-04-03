package com.wrathur.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.po.User;
import com.wrathur.user.domain.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserService extends IService<User> {
    // 创建用户
    void createUser(UserDTO userDTO);

    // 登录用户
    UserVO loginUser(UserDTO userDTO);

    // 退出登录用户
    void logoutUser();

    // 修改用户
    void modifyUser(UserDTO userDTO);

    // 删除用户
    void deleteUser();

    // 获取用户分页
    IPage<UserVO> getUserPages(UserQueryDTO userQueryDTO);

    // 通过关键字获取搜索用户分页
    IPage<UserVO> getSearchUserPagesByKeyword(UserQueryDTO userQueryDTO);

    // 获取用户详情
    UserVO getUserDetail();

    // 上传用户头像
    void uploadUserPortrait(Integer id, MultipartFile file) throws IOException;

    // 通过ID获取用户名
    String getUsernameById(Integer id);
}