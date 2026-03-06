package com.wrathur.instruction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.result.Result;
import com.wrathur.instruction.domain.dto.CourseResourceDTO;
import com.wrathur.instruction.domain.dto.CourseResourceQueryDTO;
import com.wrathur.instruction.domain.vo.CourseResourceVO;
import com.wrathur.instruction.service.ICourseResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/courseResource")
@Api(tags = "教学资源服务")
public class CourseResourceController {

    private final ICourseResourceService courseResourceService;

    @PostMapping("/create")
    @ApiOperation("创建教学资源")
    public Result<String> createCourseResource(@RequestBody CourseResourceDTO courseResourceDTO) {
        log.info("创建教学资源：{}", courseResourceDTO);
        courseResourceService.createCourseResource(courseResourceDTO);
        return Result.success();
    }

    @PatchMapping("/modify/{id}")
    @ApiOperation("修改教学资源")
    public Result<String> modifyCourseResource(@PathVariable Integer id, @RequestBody CourseResourceDTO courseResourceDTO) {
        log.info("修改教学资源{}：{}", id, courseResourceDTO);
        courseResourceService.modifyCourseResource(id, courseResourceDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除教学资源")
    public Result<String> deleteCourseResource(@PathVariable Integer id) {
        log.info("删除教学资源：{}", id);
        courseResourceService.deleteCourseResource(id);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("获取教学资源分页")
    public Result<IPage<CourseResourceVO>> getCourseResourcePages(@RequestBody CourseResourceQueryDTO courseResourceQueryDTO) {
        log.info("获取教学资源分页：{}", courseResourceQueryDTO);
        return Result.success(courseResourceService.getCourseResourcePages(courseResourceQueryDTO));
    }

    @GetMapping("/{id}")
    @ApiOperation("获取教学资源详情")
    public Result<CourseResourceVO> getCourseResourceDetail(@PathVariable Integer id) {
        log.info("获取教学资源详情：{}", id);
        return Result.success(courseResourceService.getCourseResourceDetail(id));
    }
}