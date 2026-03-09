package com.wrathur.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.po.User;
import com.wrathur.user.domain.vo.UserVO;
import com.wrathur.user.mapper.UserMapper;
import com.wrathur.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

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

    // 修改用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUser(Integer id, UserDTO userDTO) {
        User user = userMapper.selectById(id);
        BeanUtils.copyProperties(userDTO, user);
        user.setUpdateTime(LocalDateTime.now());

        LambdaUpdateWrapper<User> modifyWrapper = new LambdaUpdateWrapper<>();
        modifyWrapper.eq(User::getId, id);
        userMapper.update(user, modifyWrapper);
    }

    // 删除用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Integer id) {
        userMapper.update(null,
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, id)
                        .set(User::getUsername, "账户已注销")
                        .set(User::getIsDeleted, true)
                        .set(User::getDeleteTime, LocalDateTime.now()));
    }

    // 获取用户分页
    @Override
    public IPage<UserVO> getUserPages(Integer id, UserQueryDTO userQueryDTO) {
        // 构建分页对象
        Page<User> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<User> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤用户自身
        pageWrapper.ne(User::getId, id);
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
    public UserVO getUserDetail(Integer id) {
        User user = userMapper.selectById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        //特殊属性需要额外赋值
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());
        userVO.setDeleteTime(user.getDeleteTime());
        return userVO;
    }

    // 通过ID获取用户名
    @Override
    public String getUsernameById(Integer id) {
        return userMapper.selectById(id).getUsername();
    }
}