package com.wrathur.instruction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.config.StorageProperties;
import com.wrathur.common.result.Result;
import com.wrathur.instruction.domain.dto.CourseResourceDTO;
import com.wrathur.instruction.domain.dto.CourseResourceQueryDTO;
import com.wrathur.instruction.domain.vo.CourseResourceVO;
import com.wrathur.instruction.service.ICourseResourceService;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courseResource")
@Api(tags = "教学资源服务")
public class CourseResourceController {

    private final ICourseResourceService courseResourceService;
    private final StorageProperties storageProperties;

    @PostMapping("/create")
    @ApiOperation("创建教学资源")
    public Result<CourseResourceVO> createCourseResource(@RequestBody CourseResourceDTO courseResourceDTO) {
        log.info("创建教学资源：{}", courseResourceDTO);
        return Result.success(courseResourceService.createCourseResource(courseResourceDTO));
    }

    @PostMapping("/modify")
    @ApiOperation("修改教学资源")
    public Result<String> modifyCourseResource(@RequestBody CourseResourceDTO courseResourceDTO) {
        log.info("修改教学资源：{}", courseResourceDTO);
        courseResourceService.modifyCourseResource(courseResourceDTO);
        return Result.success();
    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除教学资源")
    public Result<String> deleteCourseResource(@PathVariable Integer id) {
        log.info("删除教学资源：{}", id);
        courseResourceService.deleteCourseResource(id);
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation("获取教学资源分页")
    public Result<IPage<CourseResourceVO>> getCourseResourcePages(@RequestBody CourseResourceQueryDTO courseResourceQueryDTO) {
        log.info("获取教学资源分页：{}", courseResourceQueryDTO);
        return Result.success(courseResourceService.getCourseResourcePages(courseResourceQueryDTO));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取教学资源详情")
    public Result<CourseResourceVO> getCourseResourceDetail(@PathVariable Integer id) {
        log.info("获取教学资源详情：{}", id);
        return Result.success(courseResourceService.getCourseResourceDetail(id));
    }

    @PostMapping("/uploadCourseResource")
    @ApiOperation("上传教学资源")
    public Result<String> uploadCourseResource(@RequestParam("id") Integer id, @RequestParam("file") MultipartFile file) {
        try {
            courseResourceService.uploadCourseResource(id, file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }

    @GetMapping("/downloadCourseResource")
    @ApiOperation("下载教学资源")
    public ResponseEntity<Resource> downloadCourseResource(@RequestParam("id") Integer id, @RequestParam("file") String file) {
        try {
            Path filePath = Paths.get(storageProperties.getRootPath(), storageProperties.getCourseResourcePath(), String.valueOf(id), file);
            System.out.println(filePath);
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println(resource);
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

    @GetMapping("/courseResources/{id}")
    @ApiOperation("通过课程获取所有未删除的教学资源")
    public List<Integer> getCourseResourceIdsByCourseId(@PathVariable Integer id) {
        log.info("通过课程{}获取所有未删除的教学资源", id);
        return courseResourceService.getCourseResourceIdsByCourseId(id);
    }
}