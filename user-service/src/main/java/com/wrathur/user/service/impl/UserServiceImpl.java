package com.wrathur.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wrathur.common.config.StorageProperties;
import com.wrathur.common.exception.BadRequestException;
import com.wrathur.common.utils.FileStorageUtils;
import com.wrathur.common.utils.UserContext;
import com.wrathur.user.config.JwtProperties;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.po.User;
import com.wrathur.user.domain.vo.UserVO;
import com.wrathur.user.mapper.UserMapper;
import com.wrathur.user.service.IUserService;
import com.wrathur.user.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final JwtTool jwtTool;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final StorageProperties storageProperties;

    // 创建用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setIsDeleted(false);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public UserVO loginUser(UserDTO userDTO) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getAccount, userDTO.getAccount()));

        if (user == null || user.getIsDeleted() == true) {
            throw new BadRequestException("用户不存在");
        }
//        log.warn(userDTO.getPassword(), user.getPassword());
//        if (user.getAccount().equals(userDTO.getAccount()) && passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
        if (user.getAccount().equals(userDTO.getAccount()) && userDTO.getPassword().equals(user.getPassword())) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            //特殊属性需要额外赋值
            userVO.setCreateTime(user.getCreateTime());
            userVO.setUpdateTime(user.getUpdateTime());
            userVO.setDeleteTime(user.getDeleteTime());
            String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
            userVO.setToken(token);
            return userVO;
        } else {
            throw new BadRequestException("用户名或密码错误");
        }
    }

    // 退出登录用户
    @Override
    public void logoutUser() {
        UserContext.removeUser();
    }

    // 修改用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUser(UserDTO userDTO) {
        User user = userMapper.selectById(UserContext.getUser());
        BeanUtils.copyProperties(userDTO, user);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user, new LambdaUpdateWrapper<User>()
                .eq(User::getId, UserContext.getUser()));
    }

    // 删除用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser() {
        userMapper.update(null,
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, UserContext.getUser())
                        .set(User::getUsername, "账户已注销")
                        .set(User::getIsDeleted, true)
                        .set(User::getDeleteTime, LocalDateTime.now()));
    }

    // 获取用户分页
    @Override
    public IPage<UserVO> getUserPages(UserQueryDTO userQueryDTO) {
        // 构建分页对象
        Page<User> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<User> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤用户自身
        pageWrapper.ne(User::getId, UserContext.getUser());
        // 过滤已删除用户
        if (userQueryDTO.getIsDeleted() != null && userQueryDTO.getIsDeleted()) {
            pageWrapper.eq(User::getIsDeleted, false);
        }

        // 模糊查询账号
        if (userQueryDTO.getAccount() != null && !userQueryDTO.getAccount().isEmpty()) {
            pageWrapper.like(User::getAccount, userQueryDTO.getAccount());
        }
        // 模糊查询用户名
        if (userQueryDTO.getUsername() != null && !userQueryDTO.getUsername().isEmpty()) {
            pageWrapper.like(User::getUsername, userQueryDTO.getUsername());
        }
        // 精确查询类型
        if (userQueryDTO.getRoleType() != null) {
            pageWrapper.eq(User::getRoleType, userQueryDTO.getRoleType());
        }

        // 范围查询创建时间
        if (userQueryDTO.getStartCreateTime() != null) {
            pageWrapper.ge(User::getCreateTime, userQueryDTO.getStartCreateTime());
        }
        if (userQueryDTO.getEndCreateTime() != null) {
            pageWrapper.le(User::getCreateTime, userQueryDTO.getEndCreateTime());
        }

        // 排序
        if (userQueryDTO.getSortType() != null && userQueryDTO.getIsAsc() != null) {
            if (userQueryDTO.getIsAsc()) {
                if (userQueryDTO.getSortType() == 0) {
                    pageWrapper.orderByAsc(User::getCreateTime);
                } else {
                    pageWrapper.orderByAsc(User::getId);
                }
            } else {
                if (userQueryDTO.getSortType() == 0) {
                    pageWrapper.orderByDesc(User::getCreateTime);
                } else {
                    pageWrapper.orderByDesc(User::getId);
                }
            }
        }

        // 执行分页查询
        IPage<User> userPage = userMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<UserVO> courseVOS = userPage.getRecords().stream()
                .map(this::convertUserToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(userPage, courseVOS);
    }

    // 通过关键字获取搜索用户分页
    @Override
    public IPage<UserVO> getSearchUserPagesByKeyword(UserQueryDTO userQueryDTO) {
        // 构建分页对象
        Page<User> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<User> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤用户自身
        pageWrapper.ne(User::getId, UserContext.getUser());
        // 过滤已删除用户
        pageWrapper.eq(User::getIsDeleted, false);

        // 模糊查询账号
        if (userQueryDTO.getAccount() != null && !userQueryDTO.getAccount().isEmpty()) {
            pageWrapper.like(User::getAccount, userQueryDTO.getAccount());
        }
        // 模糊查询用户名
        if (userQueryDTO.getUsername() != null && !userQueryDTO.getUsername().isEmpty()) {
            pageWrapper.like(User::getUsername, userQueryDTO.getUsername());
        }

        // 排序
        if (userQueryDTO.getSortType() != null && userQueryDTO.getIsAsc() != null) {
            if (userQueryDTO.getIsAsc()) {
                if (userQueryDTO.getSortType() == 0) {
                    pageWrapper.orderByAsc(User::getCreateTime);
                } else {
                    pageWrapper.orderByAsc(User::getId);
                }
            } else {
                if (userQueryDTO.getSortType() == 0) {
                    pageWrapper.orderByDesc(User::getCreateTime);
                } else {
                    pageWrapper.orderByDesc(User::getId);
                }
            }
        }

        // 执行分页查询
        IPage<User> userPage = userMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<UserVO> courseVOS = userPage.getRecords().stream()
                .map(this::convertUserToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(userPage, courseVOS);
    }

    //转化VO
    private UserVO convertUserToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());
        userVO.setDeleteTime(user.getDeleteTime());
        return userVO;
    }

    //转化分页结果
    private IPage<UserVO> convertPageResult(IPage<User> userPage, List<UserVO> userVOS) {
        IPage<UserVO> resultPage = new Page<>();
        resultPage.setRecords(userVOS);
        resultPage.setTotal(userPage.getTotal());
        resultPage.setPages(userPage.getPages());
        resultPage.setCurrent(userPage.getCurrent());
        resultPage.setSize(userPage.getSize());
        return resultPage;
    }

    // 获取用户详情
    @Override
    public UserVO getUserDetail() {
        User user = userMapper.selectById(UserContext.getUser());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        //特殊属性需要额外赋值
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());
        userVO.setDeleteTime(user.getDeleteTime());
        return userVO;
    }

    // 上传用户头像
    @Override
    public void uploadUserPortrait(Integer id, MultipartFile file) throws IOException {
        User user = userMapper.selectById(id);
        if (user.getPortrait() != null && !user.getPortrait().isEmpty()) {
            FileStorageUtils.deleteFile(storageProperties.getRootPath() + storageProperties.getUserPath(), String.valueOf(id), user.getPortrait());
        }
        FileStorageUtils.saveFile(storageProperties.getRootPath() + storageProperties.getUserPath() + "/" + id, file);
        userMapper.update(null,
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, id)
                        .set(User::getPortrait, file.getOriginalFilename()));
    }

    // 通过ID获取用户名
    @Override
    public String getUsernameById(Integer id) {
        return userMapper.selectById(id).getUsername();
    }
}