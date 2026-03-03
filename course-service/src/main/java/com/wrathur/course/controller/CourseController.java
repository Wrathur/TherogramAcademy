package com.wrathur.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.service.ICourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/course")
@Api(tags = "课程服务")
public class CourseController {

    private final ICourseService courseService;

    public CourseController(ICourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @ApiOperation("创建课程")
    public void createCourse(@RequestBody CourseDTO courseDTO) {
        courseService.createCourse(courseDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改课程")
    public void modifyCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        courseDTO.setId(id);
        courseService.modifyCourse(courseDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除课程")
    public void deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取课程详情")
    public CourseVO getCourseDetail(@PathVariable Integer id) {
        return courseService.getCourseDetail(id);
    }

    @GetMapping("/page")
    @ApiOperation("获取课程列表")
    public IPage<CourseVO> getAllCourses(CourseQueryDTO courseQueryDTO) {
        return courseService.getAllCourses(courseQueryDTO);
    }
}