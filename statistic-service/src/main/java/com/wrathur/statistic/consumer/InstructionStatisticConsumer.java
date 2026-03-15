package com.wrathur.statistic.consumer;

import com.wrathur.common.event.StatisticEvent;
import com.wrathur.statistic.service.IInstructionStatisticService;
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
public class InstructionStatisticConsumer {

    private final IInstructionStatisticService statisticService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 全站学习时长
    private static final String KEY_OVERALL_STUDY_TIME = "stat:instruction:overall:studyTime";
    // 个人学习时长前缀
    private static final String KEY_PERSONAL_STUDY_TIME_PREFIX = "stat:instruction:personal:studyTime:";
    // 全站成绩平均值
    private static final String KEY_OVERALL_SCORE_AVG = "stat:instruction:overall:scoreAvg";
    // 个人成绩平均值前缀
    private static final String KEY_PERSONAL_SCORE_AVG_PREFIX = "stat:instruction:personal:scoreAvg:";
    // 全站成绩排行榜
    private static final String KEY_OVERALL_SCORE_RANK = "stat:instruction:overall:scoreRank";
    // 个人成绩排行榜前缀
    private static final String KEY_PERSONAL_SCORE_RANK_PREFIX = "stat:instruction:personal:scoreRank:";
    // 全站成绩分段
    private static final String KEY_OVERALL_SCORE_SECTIONAL = "stat:instruction:overall:scoreSectional";
    // 个人成绩分段前缀
    private static final String KEY_PERSONAL_SCORE_SECTIONAL_PREFIX = "stat:instruction:personal:scoreSectional:";

    @RabbitListener(queues = "statistic.instruction.queue")
    public void handleInstructionEvent(StatisticEvent event) {
        try {
            log.info("Received instruction event: {}", event);
            List<Integer> userIds = event.getUserIds();
            switch (event.getType()) {
                case "COURSE_DELETED", "COURSE_SELECTED", "COURSE_DESELECTED":
                    updateOverallStudyTime();
                    updateOverallScoreAvg();
                    updateOverallScoreRank();
                    updateOverallScoreSectional();
                    if (userIds != null && !userIds.isEmpty()) {
                        userIds.forEach(userId -> {
                            updatePersonalStudyTime(userId);
                            updatePersonalScoreAvg(userId);
                            updatePersonalScoreRank(userId);
                            updatePersonalScoreSectional(userId);
                        });
                    }
                    break;
                case "COURSE_PROGRESS_UPDATED":
                    updateOverallStudyTime();
                    if (userIds != null && !userIds.isEmpty()) {
                        userIds.forEach(this::updatePersonalStudyTime);
                    }
                case "COURSE_EVALUATED":
                    updateOverallScoreAvg();
                    updateOverallScoreRank();
                    updateOverallScoreSectional();
                    if (userIds != null && !userIds.isEmpty()) {
                        userIds.forEach(userId -> {
                            updatePersonalScoreAvg(userId);
                            updatePersonalScoreRank(userId);
                            updatePersonalScoreSectional(userId);
                        });
                    }
                    break;
                default:
                    log.warn("Unknown instruction event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing course event: {}", event, e);
        }
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updateOverallStudyTime() {
        Integer total = statisticService.overallStudyTimeStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_STUDY_TIME, total, 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_PROGRESS_UPDATED
    private void updatePersonalStudyTime(Integer id) {
        Integer time = statisticService.personalStudyTimeStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_STUDY_TIME_PREFIX + id, time, 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_EVALUATED
    private void updateOverallScoreAvg() {
        BigDecimal avg = statisticService.overallScoreAverageStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_SCORE_AVG, avg, 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_EVALUATED
    private void updatePersonalScoreAvg(Integer id) {
        BigDecimal avg = statisticService.personalScoreAverageStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_SCORE_AVG_PREFIX + id, avg, 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_EVALUATED
    private void updateOverallScoreRank() {
        Map<String, BigDecimal> rank = statisticService.overallScoreRankStatistic();
        if (rank != null && !rank.isEmpty()) {
            // 清除旧数据避免累加
            redisTemplate.delete(KEY_OVERALL_SCORE_RANK);
            rank.forEach((username, avgScore) ->
                    redisTemplate.opsForZSet().add(KEY_OVERALL_SCORE_RANK, username, avgScore.doubleValue()));
            redisTemplate.expire(KEY_OVERALL_SCORE_RANK, 1, TimeUnit.HOURS);
        }
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_EVALUATED
    private void updatePersonalScoreRank(Integer id) {
        Map<String, BigDecimal> rank = statisticService.personalScoreRankStatistic(id);
        if (rank != null && !rank.isEmpty()) {
            // 清除旧数据避免累加
            String key = KEY_PERSONAL_SCORE_RANK_PREFIX + id;
            redisTemplate.delete(key);
            rank.forEach((courseName, avgScore) ->
                    redisTemplate.opsForZSet().add(key, courseName, avgScore.doubleValue()));
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_EVALUATED
    private void updateOverallScoreSectional() {
        List<Integer> sectional = statisticService.overallScoreSectionalStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_SCORE_SECTIONAL, sectional, 1, TimeUnit.HOURS);
    }

    // 事件类型：COURSE_DELETED COURSE_SELECTED COURSE_DESELECTED COURSE_EVALUATED
    private void updatePersonalScoreSectional(Integer id) {
        List<Integer> sectional = statisticService.personalScoreSectionalStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_SCORE_SECTIONAL_PREFIX + id, sectional, 1, TimeUnit.HOURS);
    }
}