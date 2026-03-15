package com.wrathur.statistic.controller;

import com.wrathur.common.result.Result;
import com.wrathur.common.utils.UserContext;
import com.wrathur.statistic.service.IInstructionStatisticService;
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
@RequestMapping("/instructionStatistic")
@Api(tags = "教学统计服务")
public class InstructionStatisticController {

    private final IInstructionStatisticService instructionStatisticService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_TEACHER_COUNT = "stat:instruction:teacherCount";
    private static final String KEY_STUDENT_COUNT = "stat:instruction:studentCount";
    private static final String KEY_OVERALL_STUDY_TIME = "stat:instruction:overall:studyTime";
    private static final String KEY_PERSONAL_STUDY_TIME_PREFIX = "stat:instruction:personal:studyTime:";
    private static final String KEY_OVERALL_SCORE_AVG = "stat:instruction:overall:scoreAvg";
    private static final String KEY_PERSONAL_SCORE_AVG_PREFIX = "stat:instruction:personal:scoreAvg:";
    private static final String KEY_OVERALL_SCORE_RANK = "stat:instruction:overall:scoreRank";
    private static final String KEY_PERSONAL_SCORE_RANK_PREFIX = "stat:instruction:personal:scoreRank:";
    private static final String KEY_OVERALL_SCORE_SECTIONAL = "stat:instruction:overall:scoreSectional";
    private static final String KEY_PERSONAL_SCORE_SECTIONAL_PREFIX = "stat:instruction:personal:scoreSectional:";

    @GetMapping("/teacherUserCount")
    @ApiOperation("教师用户数量统计")
    public Result<Integer> teacherUserCountStatistic() {
        log.info("教师用户数量统计");
        Integer count = (Integer) redisTemplate.opsForValue().get(KEY_TEACHER_COUNT);
        if (count == null) {
            log.info("/从数据库获取");
            count = instructionStatisticService.teacherUserCountStatistic();
            redisTemplate.opsForValue().set(KEY_TEACHER_COUNT, count, 1, TimeUnit.HOURS);
        }
        return Result.success(count);
    }

    @GetMapping("/studentUserCount")
    @ApiOperation("学生用户数量统计")
    public Result<Integer> studentUserCountStatistic() {
        log.info("学生用户数量统计");
        Integer count = (Integer) redisTemplate.opsForValue().get(KEY_STUDENT_COUNT);
        if (count == null) {
            log.info("/从数据库获取");
            count = instructionStatisticService.studentUserCountStatistic();
            redisTemplate.opsForValue().set(KEY_STUDENT_COUNT, count, 1, TimeUnit.HOURS);
        }
        return Result.success(count);
    }

    @GetMapping("/overallStudyTime")
    @ApiOperation("全站学习时长统计")
    public Result<Integer> overallStudyTimeStatistic() {
        log.info("全站学习时长统计");
        Integer total = (Integer) redisTemplate.opsForValue().get(KEY_OVERALL_STUDY_TIME);
        if (total == null) {
            log.info("/从数据库获取");
            total = instructionStatisticService.overallStudyTimeStatistic();
            redisTemplate.opsForValue().set(KEY_OVERALL_STUDY_TIME, total, 1, TimeUnit.HOURS);
        }
        return Result.success(total);
    }

    @GetMapping("/personalStudyTime")
    @ApiOperation("个人学习时长统计")
    public Result<Integer> personalStudyTimeStatistic() {
        log.info("个人学习时长统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_STUDY_TIME_PREFIX + userId;
        Integer time = (Integer) redisTemplate.opsForValue().get(key);
        if (time == null) {
            log.info("/从数据库获取");
            time = instructionStatisticService.personalStudyTimeStatistic(userId);
            redisTemplate.opsForValue().set(key, time, 1, TimeUnit.HOURS);
        }
        return Result.success(time);
    }

    @GetMapping("/overallScoreAverage")
    @ApiOperation("全站成绩平均统计")
    public Result<BigDecimal> overallScoreAverageStatistic() {
        log.info("全站成绩平均统计");
        BigDecimal avg = (BigDecimal) redisTemplate.opsForValue().get(KEY_OVERALL_SCORE_AVG);
        if (avg == null) {
            log.info("/从数据库获取");
            avg = instructionStatisticService.overallScoreAverageStatistic();
            redisTemplate.opsForValue().set(KEY_OVERALL_SCORE_AVG, avg, 1, TimeUnit.HOURS);
        }
        return Result.success(avg);
    }

    @GetMapping("/personalScoreAverage")
    @ApiOperation("个人成绩平均统计")
    public Result<BigDecimal> personalScoreAverageStatistic() {
        log.info("个人成绩平均统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_SCORE_AVG_PREFIX + userId;
        BigDecimal avg = (BigDecimal) redisTemplate.opsForValue().get(key);
        if (avg == null) {
            log.info("/从数据库获取");
            avg = instructionStatisticService.personalScoreAverageStatistic(userId);
            redisTemplate.opsForValue().set(key, avg, 1, TimeUnit.HOURS);
        }
        return Result.success(avg);
    }

    @GetMapping("/overallScoreRank")
    @ApiOperation("全站成绩排行统计")
    public Result<Map<String, BigDecimal>> overallScoreRankStatistic() {
        log.info("全站成绩排行统计");
        Set<Object> members = redisTemplate.opsForZSet().reverseRange(KEY_OVERALL_SCORE_RANK, 0, 9);
        Map<String, BigDecimal> rank = new java.util.LinkedHashMap<>();
        if (members != null && !members.isEmpty()) {
            for (Object member : members) {
                Double score = redisTemplate.opsForZSet().score(KEY_OVERALL_SCORE_RANK, member);
                if(score == null) continue;
                rank.put(member.toString(), BigDecimal.valueOf(score));
            }
        } else {
            log.info("/从数据库获取");
            rank = instructionStatisticService.overallScoreRankStatistic();
            if (rank != null && !rank.isEmpty()) {
                // 清除旧数据避免累加
                redisTemplate.delete(KEY_OVERALL_SCORE_RANK);
                rank.forEach((username, avgScore) ->
                        redisTemplate.opsForZSet().add(KEY_OVERALL_SCORE_RANK, username, avgScore.doubleValue()));
                redisTemplate.expire(KEY_OVERALL_SCORE_RANK, 1, TimeUnit.HOURS);
            }
        }
        return Result.success(rank);
    }

    @GetMapping("/personalScoreRank")
    @ApiOperation("个人成绩排行统计")
    public Result<Map<String, BigDecimal>> personalScoreRankStatistic() {
        log.info("个人成绩排行统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_SCORE_RANK_PREFIX + userId;
        Set<Object> members = redisTemplate.opsForZSet().reverseRange(key, 0, 9);
        Map<String, BigDecimal> rank = new java.util.LinkedHashMap<>();
        if (members != null && !members.isEmpty()) {
            for (Object member : members) {
                Double score = redisTemplate.opsForZSet().score(key, member);
                if(score == null) continue;
                rank.put(member.toString(), BigDecimal.valueOf(score));
            }
        } else {
            log.info("/从数据库获取");
            rank = instructionStatisticService.personalScoreRankStatistic(userId);
            if (rank != null && !rank.isEmpty()) {
                // 清除旧数据避免累加
                redisTemplate.delete(key);
                rank.forEach((courseName, avgScore) ->
                        redisTemplate.opsForZSet().add(key, courseName, avgScore.doubleValue()));
                redisTemplate.expire(key, 1, TimeUnit.HOURS);
            }
        }
        return Result.success(rank);
    }

    @GetMapping("/overallScoreSectional")
    @ApiOperation("全站成绩分段统计")
    public Result<List<Integer>> overallScoreSectionalStatistic() {
        log.info("全站成绩分段统计");
        List<Integer> sectional = (List<Integer>) redisTemplate.opsForValue().get(KEY_OVERALL_SCORE_SECTIONAL);
        if (sectional == null || sectional.isEmpty()) {
            log.info("/从数据库获取");
            sectional = instructionStatisticService.overallScoreSectionalStatistic();
            redisTemplate.opsForValue().set(KEY_OVERALL_SCORE_SECTIONAL, sectional, 1, TimeUnit.HOURS);
        }
        return Result.success(sectional);
    }

    @GetMapping("/personalScoreSectional")
    @ApiOperation("个人成绩分段统计")
    public Result<List<Integer>> personalScoreSectionalStatistic() {
        log.info("个人成绩分段统计");
        Integer userId = UserContext.getUser();
        if (userId == null) return Result.error("未登录");
        String key = KEY_PERSONAL_SCORE_SECTIONAL_PREFIX + userId;
        List<Integer> sectional = (List<Integer>) redisTemplate.opsForValue().get(key);
        if (sectional == null || sectional.isEmpty()) {
            log.info("/从数据库获取");
            sectional = instructionStatisticService.personalScoreSectionalStatistic(userId);
            redisTemplate.opsForValue().set(key, sectional, 1, TimeUnit.HOURS);
        }
        return Result.success(sectional);
    }
}