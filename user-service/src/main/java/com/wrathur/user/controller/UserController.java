package com.wrathur.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.result.Result;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.vo.UserVO;
import com.wrathur.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(tags = "用户服务")
public class UserController {

    private final IUserService userService;

    @PostMapping("/create")
    @ApiOperation("创建用户")
    public Result<String> createUser(@RequestBody UserDTO userDTO) {
        log.info("创建用户：{}", userDTO);
        userService.createUser(userDTO);
        return Result.success();
    }

    @PostMapping("/login")
    @ApiOperation("登录用户")
    public Result<UserVO> loginUser(@RequestBody UserDTO userDTO) {
        log.info("登录用户：{}", userDTO);
        return Result.success(userService.loginUser(userDTO));
    }

    @PatchMapping("/modify")
    @ApiOperation("修改用户")
    public Result<String> modifyUser(@RequestBody UserDTO userDTO) {
        log.info("修改用户：{}", userDTO);
        userService.modifyUser( userDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除用户")
    public Result<String> deleteUser() {
        log.info("删除用户");
        userService.deleteUser();
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation("获取用户分页")
    public Result<IPage<UserVO>> getUserPages(@RequestBody UserQueryDTO courseResourceQueryDTO) {
        log.info("获取用户分页：{}", courseResourceQueryDTO);
        return Result.success(userService.getUserPages(courseResourceQueryDTO));
    }

    @GetMapping("/detail")
    @ApiOperation("获取用户详情")
    public Result<UserVO> getUserDetail() {
        log.info("获取用户详情");
        return Result.success(userService.getUserDetail());
    }

    @GetMapping("/username/{id}")
    @ApiOperation("通过ID获取用户名")
    public String getUsernameById(@PathVariable Integer id) {
        log.info("通过ID{}获取用户名", id);
        return userService.getUsernameById(id);
    }
}