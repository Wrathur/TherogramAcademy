package com.wrathur.statistic.consumer;

import com.wrathur.statistic.event.StatisticEvent;
import com.wrathur.statistic.service.IInstructionStatisticService;
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
public class InstructionStatisticConsumer {

    private final IInstructionStatisticService statisticService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 教师用户数量
    private static final String KEY_TEACHER_COUNT = "stat:instruction:teacherCount";
    // 学生用户数量
    private static final String KEY_STUDENT_COUNT = "stat:instruction:studentCount";
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
        log.info("Received instruction event: {}", event);
        switch (event.getType()) {
            case "USER_REGISTERED":
                updateUserCount(event);
                break;
            case "STUDY_TIME_UPDATED":
                updateOverallStudyTime();
                if (event.getUserId() != null) {
                    updatePersonalStudyTime(event.getUserId());
                }
                break;
            case "SCORE_UPDATED":
                // 平均值统计更新
                updateOverallScoreAvg();
                if (event.getUserId() != null) {
                    updatePersonalScoreAvg(event.getUserId());
                }
                // 排行榜统计更新
                updateOverallScoreRank(event.getUserId(), event.getScore());
                if (event.getUserId() != null) {
                    updatePersonalScoreRank(event.getUserId(), event.getScore());
                }
                // 分段统计更新
                updateOverallScoreSectional();
                if (event.getUserId() != null) {
                    updatePersonalScoreSectional(event.getUserId());
                }
                break;
            default:
                log.warn("Unknown instruction event type: {}", event.getType());
        }
    }

    private void updateUserCount(StatisticEvent event) {
        if (event.getUserRole().equals("TEACHER")) {
            Integer teacherCount = statisticService.teacherUserCountStatistic();
            redisTemplate.opsForValue().set(KEY_TEACHER_COUNT, teacherCount, 1, TimeUnit.HOURS);
        } else {
            Integer studentCount = statisticService.studentUserCountStatistic();
            redisTemplate.opsForValue().set(KEY_STUDENT_COUNT, studentCount, 1, TimeUnit.HOURS);
        }
    }

    private void updateOverallStudyTime() {
        Integer total = statisticService.overallStudyTimeStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_STUDY_TIME, total, 1, TimeUnit.HOURS);
    }

    private void updatePersonalStudyTime(Integer id) {
        Integer time = statisticService.personalStudyTimeStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_STUDY_TIME_PREFIX + id, time, 1, TimeUnit.HOURS);
    }

    private void updateOverallScoreAvg() {
        BigDecimal avg = statisticService.overallScoreAverageStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_SCORE_AVG, avg, 1, TimeUnit.HOURS);
    }

    private void updatePersonalScoreAvg(Integer id) {
        BigDecimal avg = statisticService.personalScoreAverageStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_SCORE_AVG_PREFIX + id, avg, 1, TimeUnit.HOURS);
    }

    private void updateOverallScoreRank(Integer id, Integer score) {
        redisTemplate.opsForZSet().add(KEY_OVERALL_SCORE_RANK, "user:" + id, score);
    }

    private void updatePersonalScoreRank(Integer id, Integer score) {
        redisTemplate.opsForZSet().add(KEY_PERSONAL_SCORE_RANK_PREFIX + id, "user:" + id, score);
    }

    private void updateOverallScoreSectional() {
        List<Integer> sectional = statisticService.overallScoreSectionalStatistic();
        redisTemplate.opsForValue().set(KEY_OVERALL_SCORE_SECTIONAL, sectional, 1, TimeUnit.HOURS);
    }

    private void updatePersonalScoreSectional(Integer id) {
        List<Integer> sectional = statisticService.personalScoreSectionalStatistic(id);
        redisTemplate.opsForValue().set(KEY_PERSONAL_SCORE_SECTIONAL_PREFIX + id, sectional, 1, TimeUnit.HOURS);
    }
}