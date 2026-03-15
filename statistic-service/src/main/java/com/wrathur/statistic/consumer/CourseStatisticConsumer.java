package com.wrathur.statistic.consumer;

import com.wrathur.statistic.event.StatisticEvent;
import com.wrathur.statistic.service.ICourseStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseStatisticConsumer {

    private final ICourseStatisticService statisticService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 全站课程数量
    private static final String KEY_OVERALL_COURSE_COUNT = "stat:course:overall:count";
    // 全站选课人数
    private static final String KEY_OVERALL_SELECT_COUNT = "stat:course:overall:selectCount";
    // 个人选课人数前缀
    private static final String KEY_PERSONAL_SELECT_COUNT_PREFIX = "stat:course:personal:selectCount:";
    // 全站课程完成率平均值
    private static final String KEY_OVERALL_COMPLETION_AVG = "stat:course:overall:completionAvg";
    // 个人课程完成率平均值前缀
    private static final String KEY_PERSONAL_COMPLETION_AVG_PREFIX = "stat:course:personal:completionAvg:";
    // 全站课程完成率排行榜
    private static final String KEY_OVERALL_COMPLETION_RANK = "stat:course:overall:completionRank";
    // 个人课程完成率排行榜前缀
    private static final String KEY_PERSONAL_COMPLETION_RANK_PREFIX = "stat:course:personal:completionRank:";
    // 全站课程完成率分段
    private static final String KEY_OVERALL_COMPLETION_SECTIONAL = "stat:course:overall:completionSectional";
    // 个人课程完成率分段前缀
    private static final String KEY_PERSONAL_COMPLETION_SECTIONAL_PREFIX = "stat:course:personal:completionSectional:";

    @RabbitListener(queues = "statistic.course.queue")
    public void handleCourseEvent(StatisticEvent event) {
        log.info("Received course event: {}", event);
        switch (event.getType()) {
            case "COURSE_CREATED":
                updateOverallCourseCount();
                break;
            case "COURSE_SELECTED":
                updateOverallSelectCount();
                if (event.getUserId() != null) {
                    updatePersonalSelectCount(event.getUserId());
                }
                break;
            case "COURSE_PROGRESS_UPDATED":
                // 平均值统计更新
                updateOverallCompletionAvg();
                if (event.getUserId() != null) {
                    updatePersonalCompletionAvg(event.getUserId());
                }
                // 排行榜统计更新
                updateOverallCompletionRank(event.getCourseId(), event.getUserId());
                if (event.getUserId() != null) {
                    updatePersonalCompletionRank(event.getCourseId(), event.getUserId());
                }
                // 分段统计更新
                updateOverallCompletionSectional();
                if (event.getUserId() != null) {
                    updatePersonalCompletionSectional(event.getUserId());
                }
                break;
            default:
                log.warn("Unknown course event type: {}", event.getType());
        }
    }

    private void updateOverallCourseCount() {
        Integer count = statisticService.overallCourseCountStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_COURSE_COUNT, count, 1, TimeUnit.HOURS);
    }

    private void updateOverallSelectCount() {
        Integer count = statisticService.overallTotalSelectCountStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_SELECT_COUNT, count, 1, TimeUnit.HOURS);
    }

    private void updatePersonalSelectCount(Integer id) {
        Integer count = statisticService.personalTotalSelectCountStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_SELECT_COUNT_PREFIX + id, count, 1, TimeUnit.HOURS);
    }

    private void updateOverallCompletionAvg() {
        BigDecimal avg = statisticService.overallCourseCompletionRateAverageStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_COMPLETION_AVG, avg, 1, TimeUnit.HOURS);
    }

    private void updatePersonalCompletionAvg(Integer id) {
        BigDecimal avg = statisticService.personalCourseCompletionRateAverageStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_COMPLETION_AVG_PREFIX + id, avg, 1, TimeUnit.HOURS);
    }

    private void updateOverallCompletionRank(Integer courseId, Integer id) {
        // 从数据库查询该课程对于当前用户的完成率
        BigDecimal completionRate = statisticService.getCourseCompletionRateForUser(courseId, id);
        redisTemplate.opsForZSet().add(KEY_OVERALL_COMPLETION_RANK, "course:" + courseId, completionRate.doubleValue());
    }

    private void updatePersonalCompletionRank(Integer courseId, Integer id) {
        // 从数据库查询该课程对于当前用户的完成率
        BigDecimal completionRate = statisticService.getCourseCompletionRateForUser(courseId, id);
        redisTemplate.opsForZSet().add(KEY_PERSONAL_COMPLETION_RANK_PREFIX + id, "course:" + courseId, completionRate.doubleValue());
    }

    private void updateOverallCompletionSectional() {
        List<Integer> sectional = statisticService.overallCourseCompletionRateSectionalStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_COMPLETION_SECTIONAL, sectional, 1, TimeUnit.HOURS);
    }

    private void updatePersonalCompletionSectional(Integer id) {
        List<Integer> sectional = statisticService.personalCourseCompletionRateSectionalStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_COMPLETION_SECTIONAL_PREFIX + id, sectional, 1, TimeUnit.HOURS);
    }
}