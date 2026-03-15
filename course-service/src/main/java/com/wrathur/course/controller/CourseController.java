package com.wrathur.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.result.Result;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.domain.vo.StudentCourseVO;
import com.wrathur.course.service.ICourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
@Api(tags = "课程服务")
public class CourseController {

    private final ICourseService courseService;

    @PostMapping("/create")
    @ApiOperation("创建课程")
    public Result<String> createCourse(@RequestBody CourseDTO courseDTO) {
        log.info("创建课程：{}", courseDTO);
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

    @PostMapping("/page")
    @ApiOperation("获取课程分页")
    public Result<IPage<CourseVO>> getCoursePages(@RequestBody CourseQueryDTO courseQueryDTO) {
        log.info("获取课程分页：{}", courseQueryDTO);
        return Result.success(courseService.getCoursePages(courseQueryDTO));
    }

    @PostMapping("/createPage")
    @ApiOperation("获取创建课程分页")
    public Result<IPage<CourseVO>> getCreateCoursePages(@RequestBody CourseQueryDTO courseQueryDTO) {
        log.info("获取创建课程分页：{}", courseQueryDTO);
        return Result.success(courseService.getCreateCoursePages(courseQueryDTO));
    }

    @PostMapping("/selectPage")
    @ApiOperation("获取选修课程分页")
    public Result<IPage<CourseVO>> getSelectCoursePages(@RequestBody StudentCourseQueryDTO studentCourseQueryDTO) {
        log.info("获取选修课程分页：{}", studentCourseQueryDTO);
        return Result.success(courseService.getSelectCoursePages(studentCourseQueryDTO));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取课程详情")
    public Result<CourseVO> getCourseDetail(@PathVariable Integer id) {
        log.info("获取课程详情：{}", id);
        return Result.success(courseService.getCourseDetail(id));
    }

    @GetMapping("/createDetail/{id}")
    @ApiOperation("获取创建课程详情")
    public Result<CourseVO> getCreateCourseDetail(@PathVariable Integer id) {
        log.info("获取创建课程详情：{}", id);
        return Result.success(courseService.getCreateCourseDetail(id));
    }

    @GetMapping("/selectDetail/{id}")
    @ApiOperation("获取选修课程详情")
    public Result<StudentCourseVO> getSelectCourseDetail(@PathVariable Integer id) {
        log.info("获取选修课程详情：{}", id);
        return Result.success(courseService.getSelectCourseDetail(id));
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
    public Result<String> selectCourse(@PathVariable Integer id) {
        log.info("选修课程：{}", id);
        courseService.selectCourse(id);
        return Result.success();
    }

    @DeleteMapping("/deselect/{courseId}")
    @ApiOperation("退选课程")
    public Result<String> deselectCourse(@PathVariable Integer courseId) {
        log.info("退选课程：{}", courseId);
        courseService.deselectCourse(courseId);
        return Result.success();
    }

    @PatchMapping("/updateProgress/{progress}/{studyTime}/{studentId}/{courseId}")
    @ApiOperation("更新课程进度")
    public Result<String> updateCourseProgress(@PathVariable Integer progress, @PathVariable Integer studyTime, @PathVariable Integer studentId, @PathVariable Integer courseId) {
        log.info("更新学生{}的课程{}进度：{},{}", studentId, courseId, progress, studyTime);
        courseService.updateCourseProgress(progress, studyTime, studentId, courseId);
        return Result.success();
    }

    @PatchMapping("/evaluate/{score}/{studentId}/{courseId}")
    @ApiOperation("评定课程")
    public Result<String> evaluateCourse(@PathVariable BigDecimal score, @PathVariable Integer studentId, @PathVariable Integer courseId) {
        log.info("评定学生{}的课程{}：{}", studentId, courseId, score);
        courseService.evaluateCourse(score, studentId, courseId);
        return Result.success();
    }

    @GetMapping("/student/{id}")
    @ApiOperation("通过课程获取所有未退选的学生")
    public List<Integer> getStudentIdsByCourseId(@PathVariable Integer id) {
        log.info("通过课程{}获取所有未退选的学生", id);
        return courseService.getStudentIdsByCourseId(id);
    }
}