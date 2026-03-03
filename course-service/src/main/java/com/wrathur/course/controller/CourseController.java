package com.wrathur.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.result.Result;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseDTO;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.service.ICourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/course")
@Api(tags = "课程服务")
public class CourseController {

    private final ICourseService courseService;

    public CourseController(ICourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    @ApiOperation("创建课程")
    public Result<String> createCourse(@RequestBody CourseDTO courseDTO) {
        log.info("新增课程：{}", courseDTO);
        courseService.createCourse(courseDTO);
        return Result.success();
    }

    @PatchMapping("/modify/{id}")
    @ApiOperation("修改课程")
    public Result<String> modifyCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        log.info("修改课程{}：{}", id, courseDTO);
        courseService.modifyCourse(id, courseDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除课程")
    public Result<String> deleteCourse(@PathVariable Integer id) {
        log.info("删除课程：{}", id);
        courseService.deleteCourse(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取课程详情")
    public Result<CourseVO> getCourseDetail(@PathVariable Integer id) {
        log.info("获取课程详情：{}", id);
        return Result.success(courseService.getCourseDetail(id));
    }

    @GetMapping("/page")
    @ApiOperation("获取课程列表")
    public Result<IPage<CourseVO>> getAllCourses(CourseQueryDTO courseQueryDTO) {
        log.info("获取课程列表：{}", courseQueryDTO);
        return Result.success(courseService.getAllCourses(courseQueryDTO));
    }

    @PatchMapping("/review/{reviewStatus}")
    @ApiOperation("审核课程")
    public Result<String> reviewCourse(@PathVariable String reviewStatus, @RequestBody CourseDTO courseDTO) {
        log.info("审核课程{}：{}", courseDTO.getId(), reviewStatus);
        courseService.reviewCourse(reviewStatus, courseDTO);
        return Result.success();
    }

    @PostMapping("/select/{id}")
    @ApiOperation("选修课程")
    public Result<String> selectCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        log.info("学生{}选修课程：{}", id, courseDTO);
        courseService.selectCourse(id, courseDTO);
        return Result.success();
    }

    @PatchMapping("/evaluate/{score}")
    @ApiOperation("评定课程")
    public Result<String> evaluateCourse(@PathVariable BigDecimal score, @RequestBody StudentCourseDTO studentCourseDTO) {
        log.info("评定学生{}的课程{}：{}", studentCourseDTO.getStudentId(), studentCourseDTO.getCourseId(), score);
        courseService.evaluateCourse(score, studentCourseDTO);
        return Result.success();
    }
}