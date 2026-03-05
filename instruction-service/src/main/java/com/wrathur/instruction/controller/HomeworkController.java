package com.wrathur.instruction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.common.result.Result;
import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.dto.HomeworkQueryDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkDTO;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import com.wrathur.instruction.service.IHomeworkService;
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

    @GetMapping("/page")
    @ApiOperation("获取作业分页")
    public Result<IPage<HomeworkVO>> getHomeworkPages(@RequestBody HomeworkQueryDTO homeworkQueryDTO) {
        log.info("获取作业列表：{}", homeworkQueryDTO);
        return Result.success(homeworkService.getHomeworkPages(homeworkQueryDTO));
    }

    @GetMapping("/{id}")
    @ApiOperation("获取作业详情")
    public Result<HomeworkVO> getHomeworkDetail(@PathVariable Integer id) {
        log.info("获取作业详情：{}", id);
        return Result.success(homeworkService.getHomeworkDetail(id));
    }

    @PostMapping("/submit/{attachment}")
    @ApiOperation("提交作业")
    public Result<String> submitHomework(@PathVariable String attachment, @RequestBody StudentHomeworkDTO studentHomeworkDTO) {
        log.info("学生{}提交作业：{}", studentHomeworkDTO.getStudentId(), attachment);
        homeworkService.submitHomework(attachment, studentHomeworkDTO);
        return Result.success();
    }

    @PatchMapping("/evaluate/{score}")
    @ApiOperation("评定作业")
    public Result<String> evaluateHomework(@PathVariable BigDecimal score, @RequestBody StudentHomeworkDTO studentHomeworkDTO) {
        log.info("评定学生{}的作业{}", studentHomeworkDTO.getStudentId(), studentHomeworkDTO.getHomeworkId());
        homeworkService.evaluateHomework(score, studentHomeworkDTO);
        return Result.success();
    }

    @GetMapping("/course/{id}")
    @ApiOperation("通过课程获取所有未删除的作业")
    public List<Integer> getHomeworkIdsByCourseId(@PathVariable Integer id) {
        log.info("通过课程{}获取所有未删除的作业", id);
        return homeworkService.getHomeworkIdsByCourseId(id);
    }
}