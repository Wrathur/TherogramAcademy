package com.wrathur.statistic.consumer;

import com.wrathur.common.event.StatisticEvent;
import com.wrathur.statistic.service.ICourseStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseStatisticConsumer {

    private final ICourseStatisticService statisticService;
    private final RedisTemplate<String, Object> redisTemplate;

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
        try {
            log.info("Received course event: {}", event);
            List<Integer> userIds = event.getUserIds();
            switch (event.getType()) {
                case "COURSE_DELETED", "COURSE_SELECTED", "COURSE_DESELECTED":
                    updateOverallSelectCount();
                    updateOverallCompletionAvg();
                    updateOverallCompletionRank();
                    updateOverallCompletionSectional();
                    if (userIds != null && !userIds.isEmpty()) {
                        userIds.forEach(userId -> {
                            updatePersonalSelectCount(userId);
                            updatePersonalCompletionAvg(userId);
                            updatePersonalCompletionRank(userId);
                            updatePersonalCompletionSectional(userId);
                        });
                    }
                    break;
                case "COURSE_PROGRESS_UPDATED":
                    updateOverallCompletionAvg();
                    updateOverallCompletionRank();
                    updateOverallCompletionSectional();
                    if (userIds != null && !userIds.isEmpty()) {
                        userIds.forEach(userId -> {
                            updatePersonalCompletionAvg(userId);
                            updatePersonalCompletionRank(userId);
                            updatePersonalCompletionSectional(userId);
                        });
                    }
                    break;
                default:
                    log.warn("Unknown course event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing course event: {}", event, e);
        }
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED
    private void updateOverallSelectCount() {
        redisTemplate.opsForValue().set(KEY_OVERALL_SELECT_COUNT,
                statisticService.overallSelectCountStatistic(), 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED
    private void updatePersonalSelectCount(Integer id) {
        redisTemplate.opsForValue().set(KEY_PERSONAL_SELECT_COUNT_PREFIX + id,
                statisticService.personalSelectCountStatistic(id), 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updateOverallCompletionAvg() {
        log.info("updateOverallCompletionAvg");
        redisTemplate.opsForValue().set(KEY_OVERALL_COMPLETION_AVG,
                statisticService.overallCourseCompletionRateAverageStatistic(), 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updatePersonalCompletionAvg(Integer id) {
        log.info("updatePersonalCompletionAvg");
        redisTemplate.opsForValue().set(KEY_PERSONAL_COMPLETION_AVG_PREFIX + id,
                statisticService.personalCourseCompletionRateAverageStatistic(id), 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updateOverallCompletionRank() {
        Map<String, BigDecimal> rank = statisticService.overallCourseCompletionRateRankStatistic();
        if (rank != null && !rank.isEmpty()) {
            // 清除旧数据避免累加
            redisTemplate.delete(KEY_OVERALL_COMPLETION_RANK);
            rank.forEach((courseName, avgProgress) ->
                    redisTemplate.opsForZSet().add(KEY_OVERALL_COMPLETION_RANK, courseName, avgProgress.doubleValue()));
            redisTemplate.expire(KEY_OVERALL_COMPLETION_RANK, 1, TimeUnit.HOURS);
        }
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updatePersonalCompletionRank(Integer id) {
        Map<String, BigDecimal> rank = statisticService.personalCourseCompletionRateRankStatistic(id);
        if (rank != null && !rank.isEmpty()) {
            // 清除旧数据避免累加
            String key = KEY_PERSONAL_COMPLETION_RANK_PREFIX + id;
            redisTemplate.delete(key);
            rank.forEach((courseName, avgProgress) ->
                    redisTemplate.opsForZSet().add(key, courseName, avgProgress.doubleValue()));
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updateOverallCompletionSectional() {
        redisTemplate.opsForValue().set(KEY_OVERALL_COMPLETION_SECTIONAL,
                statisticService.overallCourseCompletionRateSectionalStatistic(), 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updatePersonalCompletionSectional(Integer id) {
        redisTemplate.opsForValue().set(KEY_PERSONAL_COMPLETION_SECTIONAL_PREFIX + id,
                statisticService.personalCourseCompletionRateSectionalStatistic(id), 1, TimeUnit.HOURS);
    }
}