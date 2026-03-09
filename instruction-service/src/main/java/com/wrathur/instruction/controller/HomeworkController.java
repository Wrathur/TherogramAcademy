package com.wrathur.instruction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/homework")
@Api(tags = "作业服务")
public class HomeworkController {

    private final IHomeworkService homeworkService;

    @PostMapping("/create")
    @ApiOperation("创建作业")
    public Result<String> createHomework(@RequestBody HomeworkDTO homeworkDTO) {
        log.info("创建作业：{}", homeworkDTO);
        homeworkService.createHomework(homeworkDTO);
        return Result.success();
    }

    @PatchMapping("/modify/{id}")
    @ApiOperation("修改作业")
    public Result<String> modifyHomework(@PathVariable Integer id, @RequestBody HomeworkDTO homeworkDTO) {
        log.info("修改作业{}：{}", id, homeworkDTO);
        homeworkService.modifyHomework(id, homeworkDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除作业")
    public Result<String> deleteHomework(@PathVariable Integer id) {
        log.info("删除作业：{}", id);
        homeworkService.deleteHomework(id);
        return Result.success();
    }

    @PostMapping("/page/{id}")
    @ApiOperation("获取作业分页")
    public Result<IPage<HomeworkVO>> getHomeworkPages(@PathVariable Integer id, @RequestBody HomeworkQueryDTO homeworkQueryDTO) {
        log.info("教师获取课程{}的作业分页：{}", id, homeworkQueryDTO);
        return Result.success(homeworkService.getHomeworkPages(id, homeworkQueryDTO));
    }

    @PostMapping("/studentPage/{id}")
    @ApiOperation("获取学生作业分页")
    public Result<IPage<HomeworkVO>> getStudentHomeworkPages(@PathVariable Integer id, @RequestBody StudentHomeworkQueryDTO studentHomeworkQueryDTO) {
        log.info("学生{}获取学生作业分页：{}", id, studentHomeworkQueryDTO);
        return Result.success(homeworkService.getStudentHomeworkPages(id, studentHomeworkQueryDTO));
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
        log.info("获取学生{}作业详情：{}", studentId, homeworkId);
        return Result.success(homeworkService.getStudentHomeworkDetail(studentId, homeworkId));
    }

    @PostMapping("/submit")
    @ApiOperation("提交作业")
    public Result<String> submitHomework(@RequestBody StudentHomeworkDTO studentHomeworkDTO) {
        log.info("学生{}提交作业", studentHomeworkDTO.getStudentId());
        homeworkService.submitHomework(studentHomeworkDTO);
        return Result.success();
    }

    @PatchMapping("/evaluate")
    @ApiOperation("评定作业")
    public Result<String> evaluateHomework(@RequestBody StudentHomeworkDTO studentHomeworkDTO) {
        log.info("评定学生{}的作业{}", studentHomeworkDTO.getStudentId(), studentHomeworkDTO.getHomeworkId());
        homeworkService.evaluateHomework(studentHomeworkDTO);
        return Result.success();
    }

    @GetMapping("/course/{id}")
    @ApiOperation("通过课程获取所有未删除的作业")
    public List<Integer> getHomeworkIdsByCourseId(@PathVariable Integer id) {
        log.info("通过课程{}获取所有未删除的作业", id);
        return homeworkService.getHomeworkIdsByCourseId(id);
    }

    @GetMapping("/courses")
    @ApiOperation("通过课程ID列表获取所有未删除的作业")
    public List<Integer> getHomeworkIdsByCourseId(@RequestBody List<Integer> ids) {
        log.info("通过课程ID列表{}获取所有未删除的作业", ids);
        return homeworkService.getHomeworkIdsByCourseIds(ids);
    }
}