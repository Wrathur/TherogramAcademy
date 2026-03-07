package com.wrathur.statistic.controller;

import com.wrathur.common.result.Result;
import com.wrathur.statistic.service.ICourseStatisticService;
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
@RequestMapping("/courseStatistic")
@Api(tags = "课程统计服务")
public class CourseStatisticController {

    private final ICourseStatisticService courseStatisticService;

    @GetMapping("/overallCourseCount")
    @ApiOperation("全站课程数量统计")
    public Result<Integer> overallCourseCountStatistic() {
        log.info("全站课程数量统计");
        return Result.success(courseStatisticService.overallCourseCountStatistic());
    }

    @GetMapping("/personalCourseCount/{id}")
    @ApiOperation("个人课程数量统计")
    public Result<Integer> personalCourseCountStatistic(@PathVariable Integer id) {
        log.info("个人课程数量统计");
        return Result.success(courseStatisticService.personalCourseCountStatistic(id));
    }

    @GetMapping("/overallSelectCount")
    @ApiOperation("全站选课人数统计")
    public Result<Integer> overallTotalSelectCountStatistic() {
        log.info("全站选课人数统计");
        return Result.success(courseStatisticService.overallTotalSelectCountStatistic());
    }

    @GetMapping("/personalSelectCount/{id}")
    @ApiOperation("个人选课人数统计")
    public Result<Integer> personalTotalSelectCountStatistic(@PathVariable Integer id) {
        log.info("个人选课人数统计");
        return Result.success(courseStatisticService.personalTotalSelectCountStatistic(id));
    }

    @GetMapping("/overallCourseCompletionRateAverage")
    @ApiOperation("全站课程完成率平均统计")
    public Result<BigDecimal> overallCourseCompletionRateAverageStatistic() {
        log.info("全站课程完成率平均统计");
        return Result.success(courseStatisticService.overallCourseCompletionRateAverageStatistic());
    }

    @GetMapping("/personalCourseCompletionRateAverage/{id}")
    @ApiOperation("个人课程完成率平均统计")
    public Result<BigDecimal> personalCourseCompletionRateAverageStatistic(@PathVariable Integer id) {
        log.info("个人课程完成率平均统计");
        return Result.success(courseStatisticService.personalCourseCompletionRateAverageStatistic(id));
    }

    @GetMapping("/overallCourseCompletionRateRank")
    @ApiOperation("全站课程完成率排行统计")
    public Result<Map<String, BigDecimal>> overallCourseCompletionRateRankStatistic() {
        log.info("全站课程完成率排行统计");
        return Result.success(courseStatisticService.overallCourseCompletionRateRankStatistic());
    }

    @GetMapping("/PersonalCourseCompletionRateRank/{id}")
    @ApiOperation("个人课程完成率排行统计")
    public Result<Map<String, BigDecimal>> personalCourseCompletionRateRankStatistic(@PathVariable Integer id) {
        log.info("个人课程完成率排行统计");
        return Result.success(courseStatisticService.personalCourseCompletionRateRankStatistic(id));
    }

    @GetMapping("/overallCourseCompletionRateSectional")
    @ApiOperation("全站课程完成率分段统计")
    public Result<List<Integer>> overallCourseCompletionRateSectionalStatistic() {
        log.info("全站课程完成率分段统计");
        return Result.success(courseStatisticService.overallCourseCompletionRateSectionalStatistic());
    }

    @GetMapping("/personalCourseCompletionRateSectional/{id}")
    @ApiOperation("个人课程完成率分段统计")
    public Result<List<Integer>> personalCourseCompletionRateSectionalStatistic(@PathVariable Integer id) {
        log.info("个人课程完成率分段统计");
        return Result.success(courseStatisticService.personalCourseCompletionRateSectionalStatistic(id));
    }
}