package com.wrathur.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.config.StorageProperties;
import com.wrathur.common.result.Result;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseDTO;
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.domain.vo.StudentCourseVO;
import com.wrathur.course.service.ICourseService;
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
@RequestMapping("/api/course")
@Api(tags = "课程服务")
public class CourseController {

    private final ICourseService courseService;
    private final StorageProperties storageProperties;

    @PostMapping("/create")
    @ApiOperation("创建课程")
    public Result<String> createCourse(@RequestBody CourseDTO courseDTO) {
        log.info("创建课程：{}", courseDTO);
        courseService.createCourse(courseDTO);
        return Result.success();
    }

    @PostMapping("/modify")
    @ApiOperation("修改课程")
    public Result<String> modifyCourse(@RequestBody CourseDTO courseDTO) {
        log.info("修改课程：{}", courseDTO);
        courseService.modifyCourse(courseDTO);
        return Result.success();
    }

    @PostMapping("/delete/{id}")
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

    @PostMapping("/searchPage")
    @ApiOperation("通过关键字获取搜索课程分页")
    public Result<IPage<CourseVO>> getSearchCoursePagesByKeyword(@RequestBody CourseQueryDTO courseQueryDTO) {
        log.info("通过关键字获取搜索课程分页：{}", courseQueryDTO);
        return Result.success(courseService.getSearchCoursePagesByKeyword(courseQueryDTO));
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

    @PostMapping("/review")
    @ApiOperation("审核课程")
    public Result<String> reviewCourse(@RequestBody CourseDTO courseDTO) {
        log.info("审核课程{}", courseDTO);
        courseService.reviewCourse(courseDTO);
        return Result.success();
    }

    @PostMapping("/select/{id}")
    @ApiOperation("选修课程")
    public Result<String> selectCourse(@PathVariable Integer id) {
        log.info("选修课程：{}", id);
        courseService.selectCourse(id);
        return Result.success();
    }

    @PostMapping("/deselect/{id}")
    @ApiOperation("退选课程")
    public Result<String> deselectCourse(@PathVariable Integer id) {
        log.info("退选课程：{}", id);
        courseService.deselectCourse(id);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("更新课程进度和学习时间")
    public Result<String> updateCourseProgressAndStudyTime(@RequestBody StudentCourseDTO studentCourseDTO) {
        log.info("更新课程进度和学习时间：{}", studentCourseDTO);
        courseService.updateCourseProgressAndStudyTime(studentCourseDTO);
        return Result.success();
    }

    @PostMapping("/evaluate")
    @ApiOperation("评定课程")
    public Result<String> evaluateCourse(@RequestBody StudentCourseDTO studentCourseDTO) {
        log.info("评定课程：{}", studentCourseDTO);
        courseService.evaluateCourse(studentCourseDTO);
        return Result.success();
    }

    @GetMapping("/recommend")
    @ApiOperation("推荐课程")
    public Result<List<CourseVO>> recommendCourse(@RequestParam("courseSubject") Integer courseSubject, @RequestParam("courseType") Integer courseType) {
        log.info("推荐课程");
        return Result.success(courseService.recommendCourse(courseSubject, courseType));
    }

    @PostMapping("/uploadCourse")
    @ApiOperation("上传课程封面")
    public Result<String> uploadCourseCover(@RequestParam("id") Integer id, @RequestParam("file") MultipartFile file) {
        try {
            courseService.uploadCourseCover(id, file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }

    @GetMapping("/downloadCourse")
    @ApiOperation("下载课程封面")
    public ResponseEntity<Resource> downloadCourseCover(@RequestParam("id") Integer id, @RequestParam("file") String file) {
        try {
            Path filePath = Paths.get(storageProperties.getRootPath(), storageProperties.getCoursePath(), String.valueOf(id), file);
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

    @GetMapping("/students/{id}")
    @ApiOperation("通过课程获取所有未退选的学生")
    public List<Integer> getStudentIdsByCourseId(@PathVariable Integer id) {
        log.info("通过课程{}获取所有未退选的学生", id);
        return courseService.getStudentIdsByCourseId(id);
    }

    @GetMapping("/createCourses/{id}")
    @ApiOperation("通过用户id获取该用户创建的课程")
    public List<CourseVO> getCreateCourseIdsByUserId(@PathVariable Integer id) {
        log.info("通过用户{}获取该用户创建的课程", id);
        return courseService.getCreateCourseIdsByUserId(id);
    }

    @GetMapping("/selectCourses/{id}")
    @ApiOperation("通过用户id获取该用户选修的课程")
    public List<CourseVO> getSelectCourseIdsByUserId(@PathVariable Integer id) {
        log.info("通过用户{}获取该用户选修的课程", id);
        return courseService.getSelectCourseIdsByUserId(id);
    }
}