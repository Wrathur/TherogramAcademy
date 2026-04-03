package com.wrathur.statistic.controller;

import com.wrathur.common.result.Result;
import com.wrathur.common.utils.UserContext;
import com.wrathur.statistic.service.ICourseStatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courseStatistic")
@Api(tags = "课程统计服务")
public class CourseStatisticController {

    private final ICourseStatisticService courseStatisticService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_OVERALL_SELECT_COUNT = "stat:course:overall:selectCount";
    private static final String KEY_PERSONAL_SELECT_COUNT_PREFIX = "stat:course:personal:selectCount:";
    private static final String KEY_OVERALL_COMPLETION_AVG = "stat:course:overall:completionAvg";
    private static final String KEY_PERSONAL_COMPLETION_AVG_PREFIX = "stat:course:personal:completionAvg:";
    private static final String KEY_OVERALL_COMPLETION_RANK = "stat:course:overall:completionRank";
    private static final String KEY_PERSONAL_COMPLETION_RANK_PREFIX = "stat:course:personal:completionRank:";
    private static final String KEY_OVERALL_COMPLETION_SECTIONAL = "stat:course:overall:completionSectional";
    private static final String KEY_PERSONAL_COMPLETION_SECTIONAL_PREFIX = "stat:course:personal:completionSectional:";

    @GetMapping("/overallCourseCount")
    @ApiOperation("全站课程数量统计")
    public Result<Integer> overallCourseCountStatistic() {
        log.info("全站课程数量统计");
        return Result.success(courseStatisticService.overallCourseCountStatistic());
    }

    @GetMapping("/personalCourseCount")
    @ApiOperation("个人课程数量统计")
    public Result<Integer> personalCourseCountStatistic() {
        log.info("个人课程数量统计");
        Integer userId = UserContext.getUser();
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(courseStatisticService.personalCourseCountStatistic(userId));
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED
    @GetMapping("/overallSelectCount")
    @ApiOperation("全站选课人数统计")
    public Result<Integer> overallSelectCountStatistic() {
        log.info("全站选课人数统计");
        Integer count = (Integer) redisTemplate.opsForValue().get(KEY_OVERALL_SELECT_COUNT);
        if (count == null) {
            log.info("/从数据库获取");
            count = courseStatisticService.overallSelectCountStatistic();
            redisTemplate.opsForValue().set(KEY_OVERALL_SELECT_COUNT, count, 1, TimeUnit.HOURS);
        }
        return Result.success(count);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED
    @GetMapping("/personalSelectCount")
    @ApiOperation("个人选课人数统计")
    public Result<Integer> personalSelectCountStatistic() {
        log.info("个人选课人数统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_SELECT_COUNT_PREFIX + userId;
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        if (count == null) {
            log.info("/从数据库获取");
            count = courseStatisticService.personalSelectCountStatistic(userId);
            redisTemplate.opsForValue().set(key, count, 1, TimeUnit.HOURS);
        }
        return Result.success(count);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    @GetMapping("/overallCourseCompletionRateAverage")
    @ApiOperation("全站课程完成率平均统计")
    public Result<BigDecimal> overallCourseCompletionRateAverageStatistic() {
        log.info("全站课程完成率平均统计");
        BigDecimal avg = (BigDecimal) redisTemplate.opsForValue().get(KEY_OVERALL_COMPLETION_AVG);
        if (avg == null) {
            log.info("/从数据库获取");
            avg = courseStatisticService.overallCourseCompletionRateAverageStatistic();
            redisTemplate.opsForValue().set(KEY_OVERALL_COMPLETION_AVG, avg, 1, TimeUnit.HOURS);
        }
        return Result.success(avg);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    @GetMapping("/personalCourseCompletionRateAverage")
    @ApiOperation("个人课程完成率平均统计")
    public Result<BigDecimal> personalCourseCompletionRateAverageStatistic() {
        log.info("个人课程完成率平均统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_COMPLETION_AVG_PREFIX + userId;
        BigDecimal avg = (BigDecimal) redisTemplate.opsForValue().get(key);
        if (avg == null) {
            log.info("/从数据库获取");
            avg = courseStatisticService.personalCourseCompletionRateAverageStatistic(userId);
            redisTemplate.opsForValue().set(key, avg, 1, TimeUnit.HOURS);
        }
        return Result.success(avg);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    @GetMapping("/overallCourseCompletionRateRank")
    @ApiOperation("全站课程完成率排行统计")
    public Result<Map<String, BigDecimal>> overallCourseCompletionRateRankStatistic() {
        log.info("全站课程完成率排行统计");
        Set<Object> members = redisTemplate.opsForZSet().reverseRange(KEY_OVERALL_COMPLETION_RANK, 0, 9);
        Map<String, BigDecimal> rank = new java.util.LinkedHashMap<>();
        if (members != null && !members.isEmpty()) {
            for (Object member : members) {
                Double score = redisTemplate.opsForZSet().score(KEY_OVERALL_COMPLETION_RANK, member);
                if (score == null) continue;
                rank.put(member.toString(), BigDecimal.valueOf(score));
            }
        } else {
            log.info("/从数据库获取");
            rank = courseStatisticService.overallCourseCompletionRateRankStatistic();
            if (rank != null && !rank.isEmpty()) {
                // 清除旧数据避免累加
                redisTemplate.delete(KEY_OVERALL_COMPLETION_RANK);
                rank.forEach((courseName, avgProgress) ->
                        redisTemplate.opsForZSet().add(KEY_OVERALL_COMPLETION_RANK, courseName, avgProgress.doubleValue()));
                redisTemplate.expire(KEY_OVERALL_COMPLETION_RANK, 1, TimeUnit.HOURS);
            }
        }
        return Result.success(rank);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    @GetMapping("/personalCourseCompletionRateRank")
    @ApiOperation("个人课程完成率排行统计")
    public Result<Map<String, BigDecimal>> personalCourseCompletionRateRankStatistic() {
        log.info("个人课程完成率排行统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_COMPLETION_RANK_PREFIX + userId;
        Set<Object> members = redisTemplate.opsForZSet().reverseRange(key, 0, 9);
        Map<String, BigDecimal> rank = new java.util.LinkedHashMap<>();
        if (members != null && !members.isEmpty()) {
            for (Object member : members) {
                Double score = redisTemplate.opsForZSet().score(key, member);
                if (score == null) continue;
                rank.put(member.toString(), BigDecimal.valueOf(score));
            }
        } else {
            log.info("/从数据库获取");
            rank = courseStatisticService.personalCourseCompletionRateRankStatistic(userId);
            if (rank != null && !rank.isEmpty()) {
                // 清除旧数据避免累加
                redisTemplate.delete(key);
                rank.forEach((courseName, avgProgress) ->
                        redisTemplate.opsForZSet().add(key, courseName, avgProgress.doubleValue()));
                redisTemplate.expire(key, 1, TimeUnit.HOURS);
            }
        }
        return Result.success(rank);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    @GetMapping("/overallCourseCompletionRateSectional")
    @ApiOperation("全站课程完成率分段统计")
    public Result<List<Integer>> overallCourseCompletionRateSectionalStatistic() {
        log.info("全站课程完成率分段统计");
        List<Integer> sectional = (List<Integer>) redisTemplate.opsForValue().get(KEY_OVERALL_COMPLETION_SECTIONAL);
        if (sectional == null || sectional.isEmpty()) {
            log.info("/从数据库获取");
            sectional = courseStatisticService.overallCourseCompletionRateSectionalStatistic();
            redisTemplate.opsForValue().set(KEY_OVERALL_COMPLETION_SECTIONAL, sectional, 1, TimeUnit.HOURS);
        }
        return Result.success(sectional);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    @GetMapping("/personalCourseCompletionRateSectional")
    @ApiOperation("个人课程完成率分段统计")
    public Result<List<Integer>> personalCourseCompletionRateSectionalStatistic() {
        log.info("个人课程完成率分段统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_COMPLETION_SECTIONAL_PREFIX + userId;
        List<Integer> sectional = (List<Integer>) redisTemplate.opsForValue().get(key);
        if (sectional == null || sectional.isEmpty()) {
            log.info("/从数据库获取");
            sectional = courseStatisticService.personalCourseCompletionRateSectionalStatistic(userId);
            redisTemplate.opsForValue().set(key, sectional, 1, TimeUnit.HOURS);
        }
        return Result.success(sectional);
    }
}