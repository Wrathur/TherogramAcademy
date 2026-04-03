package com.wrathur.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.config.StorageProperties;
import com.wrathur.common.result.Result;
import com.wrathur.user.domain.dto.UserDTO;
import com.wrathur.user.domain.dto.UserQueryDTO;
import com.wrathur.user.domain.vo.UserVO;
import com.wrathur.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Api(tags = "用户服务")
public class UserController {

    private final IUserService userService;
    private final StorageProperties storageProperties;

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

    @PostMapping("/logout")
    @ApiOperation("退出登录用户")
    public Result<String> logoutUser() {
        log.info("退出登录用户");
        userService.logoutUser();
        return Result.success();
    }

    @PostMapping("/modify")
    @ApiOperation("修改用户")
    public Result<String> modifyUser(@RequestBody UserDTO userDTO) {
        log.info("修改用户：{}", userDTO);
        userService.modifyUser(userDTO);
        return Result.success();
    }

    @PostMapping("/delete")
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

    @PostMapping("/searchPage")
    @ApiOperation("通过关键字获取搜索用户分页")
    public Result<IPage<UserVO>> getSearchUserPagesByKeyword(@RequestBody UserQueryDTO userQueryDTO) {
        log.info("通过关键字获取搜索课程分页：{}", userQueryDTO);
        return Result.success(userService.getSearchUserPagesByKeyword(userQueryDTO));
    }

    @GetMapping("/detail")
    @ApiOperation("获取用户详情")
    public Result<UserVO> getUserDetail() {
        log.info("获取用户详情");
        return Result.success(userService.getUserDetail());
    }

    @PostMapping("/uploadUser")
    @ApiOperation("上传用户头像")
    public Result<String> uploadUserPortrait(@RequestParam("id") Integer id, @RequestParam("file") MultipartFile file) {
        try {
            userService.uploadUserPortrait(id, file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }

    @GetMapping("/downloadUser/{relativePath}")
    @ApiOperation("下载用户头像")
    public ResponseEntity<Resource> downloadUserPortrait(@PathVariable String relativePath) {
        try {
            Path filePath = Paths.get(storageProperties.getRootPath(), relativePath);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/username/{id}")
    @ApiOperation("通过ID获取用户名")
    public String getUsernameById(@PathVariable Integer id) {
        log.info("通过ID{}获取用户名", id);
        return userService.getUsernameById(id);
    }
}