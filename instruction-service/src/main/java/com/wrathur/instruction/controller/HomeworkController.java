package com.wrathur.instruction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.config.StorageProperties;
import com.wrathur.common.result.Result;
import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.dto.HomeworkQueryDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkQueryDTO;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import com.wrathur.instruction.domain.vo.StudentHomeworkVO;
import com.wrathur.instruction.service.IHomeworkService;
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
@RequestMapping("/api/homework")
@Api(tags = "作业服务")
public class HomeworkController {

    private final IHomeworkService homeworkService;
    private final StorageProperties storageProperties;

    @PostMapping("/create")
    @ApiOperation("创建作业")
    public Result<String> createHomework(@RequestBody HomeworkDTO homeworkDTO) {
        log.info("创建作业：{}", homeworkDTO);
        homeworkService.createHomework(homeworkDTO);
        return Result.success();
    }

    @PostMapping("/modify")
    @ApiOperation("修改作业")
    public Result<String> modifyHomework(@RequestBody HomeworkDTO homeworkDTO) {
        log.info("修改作业：{}", homeworkDTO);
        homeworkService.modifyHomework(homeworkDTO);
        return Result.success();
    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除作业")
    public Result<String> deleteHomework(@PathVariable Integer id) {
        log.info("删除作业：{}", id);
        homeworkService.deleteHomework(id);
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation("获取作业分页")
    public Result<IPage<HomeworkVO>> getHomeworkPages(@RequestBody HomeworkQueryDTO homeworkQueryDTO) {
        log.info("获取作业分页：{}", homeworkQueryDTO);
        return Result.success(homeworkService.getHomeworkPages(homeworkQueryDTO));
    }

    @PostMapping("/studentPage")
    @ApiOperation("获取学生作业分页")
    public Result<IPage<HomeworkVO>> getStudentHomeworkPages(@RequestBody StudentHomeworkQueryDTO studentHomeworkQueryDTO) {
        log.info("获取学生作业分页：{}", studentHomeworkQueryDTO);
        return Result.success(homeworkService.getStudentHomeworkPages(studentHomeworkQueryDTO));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取作业详情")
    public Result<HomeworkVO> getHomeworkDetail(@PathVariable Integer id) {
        log.info("获取作业详情：{}", id);
        return Result.success(homeworkService.getHomeworkDetail(id));
    }

    @GetMapping("/studentDetail/{studentId}/{homeworkId}")
    @ApiOperation("获取学生作业详情")
    public Result<StudentHomeworkVO> getStudentHomeworkDetail(@PathVariable Integer studentId, @PathVariable Integer homeworkId) {
        log.info("获取学生{}的作业详情：{}", studentId, homeworkId);
        return Result.success(homeworkService.getStudentHomeworkDetail(studentId, homeworkId));
    }

    @PostMapping("/submit")
    @ApiOperation("提交作业")
    public Result<String> submitHomework(@RequestBody StudentHomeworkDTO studentHomeworkDTO) {
        log.info("提交作业：{}", studentHomeworkDTO);
        homeworkService.submitHomework(studentHomeworkDTO);
        return Result.success();
    }

    @PostMapping("/evaluate")
    @ApiOperation("评定作业")
    public Result<String> evaluateHomework(@RequestBody StudentHomeworkDTO studentHomeworkDTO) {
        log.info("评定作业：{}", studentHomeworkDTO);
        homeworkService.evaluateHomework(studentHomeworkDTO);
        return Result.success();
    }

    @GetMapping("/remind")
    @ApiOperation("提醒作业")
    public Result<List<HomeworkVO>> getHomeworkIdsByCourseId() {
        log.info("提醒作业");
        return Result.success(homeworkService.remindHomework());
    }

    @PostMapping("/uploadHomework")
    @ApiOperation("上传作业附件")
    public Result<String> uploadHomeworkAttachment(@RequestParam("id") Integer id, @RequestParam("file") MultipartFile file) {
        try {
            homeworkService.uploadHomeworkAttachment(id, file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }

    @GetMapping({"/downloadHomework/{relativePath:^(?!.*\\\\.js$).+}", "/downloadStudentHomework/{relativePath:^(?!.*\\\\.js$).+}"})
    @ApiOperation("下载作业/学生作业附件")
    public ResponseEntity<Resource> downloadHomeworkOrStudentHomeworkAttachment(@PathVariable String relativePath) {
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

    @PostMapping("/uploadStudentHomework")
    @ApiOperation("上传学生作业附件")
    public Result<String> uploadStudentHomeworkAttachment(@RequestParam("studentId") Integer studentId, @RequestParam("homeworkId") Integer homeworkId, @RequestParam("file") MultipartFile file) {
        try {
            homeworkService.uploadStudentHomeworkAttachment(studentId, homeworkId, file);
            return Result.success();
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }
    }

//    @GetMapping("/downloadStudentHomework/{relativePath}")
//    @ApiOperation("下载学生作业附件")
//    public ResponseEntity<Resource> downloadStudentHomeworkAttachment(@PathVariable String relativePath) {
//        try {
//            Path filePath = Paths.get(storageProperties.getRootPath(), relativePath);
//            Resource resource = new UrlResource(filePath.toUri());
//            if (resource.exists()) {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (MalformedURLException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @GetMapping("/homeworks/{id}")
    @ApiOperation("通过课程获取所有未删除的作业")
    public List<Integer> getHomeworkIdsByCourseId(@PathVariable Integer id) {
        log.info("通过课程{}获取所有未删除的作业", id);
        return homeworkService.getHomeworkIdsByCourseId(id);
    }

    @PostMapping("/homeworks")
    @ApiOperation("通过课程ID列表获取所有未删除的作业")
    public List<Integer> getHomeworkIdsByCourseId(@RequestBody List<Integer> ids) {
        log.info("通过课程ID列表{}获取所有未删除的作业", ids);
        return homeworkService.getHomeworkIdsByCourseIds(ids);
    }
}