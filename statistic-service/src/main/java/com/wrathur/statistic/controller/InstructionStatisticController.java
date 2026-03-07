package com.wrathur.statistic.controller;

import com.wrathur.common.result.Result;
import com.wrathur.statistic.service.IInstructionStatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/instructionStatistic")
@Api(tags = "教学统计服务")
public class InstructionStatisticController {

    private final IInstructionStatisticService instructionStatisticService;

    @GetMapping("/teacherUserCount")
    @ApiOperation("教师用户数量统计")
    public Result<Integer> teacherUserCountStatistic() {
        log.info("教师用户数量统计");
        return Result.success(instructionStatisticService.teacherUserCountStatistic());
    }

    @GetMapping("/studentUserCount")
    @ApiOperation("学生用户数量统计")
    public Result<Integer> studentUserCountStatistic() {
        log.info("学生用户数量统计");
        return Result.success(instructionStatisticService.studentUserCountStatistic());
    }

    @GetMapping("/overallStudyTime")
    @ApiOperation("全站学习时长统计")
    public Result<Integer> overallStudyTimeStatistic() {
        log.info("全站学习时长统计");
        return Result.success(instructionStatisticService.overallStudyTimeStatistic());
    }

    @GetMapping("/personalStudyTime/{id}")
    @ApiOperation("个人学习时长统计")
    public Result<Integer> personalStudyTimeStatistic(@PathVariable Integer id) {
        log.info("个人学习时长统计");
        return Result.success(instructionStatisticService.personalStudyTimeStatistic(id));
    }

    @GetMapping("/overallScoreAverage")
    @ApiOperation("全站成绩平均统计")
    public Result<BigDecimal> overallScoreAverageStatistic() {
        log.info("全站成绩平均统计");
        return Result.success(instructionStatisticService.overallScoreAverageStatistic());
    }

    @GetMapping("/personalScoreAverage/{id}")
    @ApiOperation("个人成绩平均统计")
    public Result<BigDecimal> personalScoreAverageStatistic(@PathVariable Integer id) {
        log.info("个人成绩平均统计");
        return Result.success(instructionStatisticService.personalScoreAverageStatistic(id));
    }

    @GetMapping("/overallScoreRank")
    @ApiOperation("全站成绩排行统计")
    public Result<Map<String, BigDecimal>> overallScoreRankStatistic() {
        log.info("全站成绩排行统计");
        return Result.success(instructionStatisticService.overallScoreRankStatistic());
    }

    @GetMapping("/personalScoreRank/{id}")
    @ApiOperation("个人成绩排行统计")
    public Result<Map<String, BigDecimal>> personalScoreRankStatistic(@PathVariable Integer id) {
        log.info("个人成绩排行统计");
        return Result.success(instructionStatisticService.personalScoreRankStatistic(id));
    }

    @GetMapping("/overallScoreSectional")
    @ApiOperation("全站成绩分段统计")
    public Result<List<Integer>> overallScoreSectionalStatistic() {
        log.info("全站成绩分段统计");
        return Result.success(instructionStatisticService.overallScoreSectionalStatistic());
    }

    @GetMapping("/personalScoreSectional/{id}")
    @ApiOperation("个人成绩分段统计")
    public Result<List<Integer>> personalScoreSectionalStatistic(@PathVariable Integer id) {
        log.info("个人成绩分段统计");
        return Result.success(instructionStatisticService.personalScoreSectionalStatistic(id));
    }
}