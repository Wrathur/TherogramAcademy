package com.wrathur.statistic.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInstructionStatisticService {
    // 教师用户数量统计
    Integer teacherUserCountStatistic();

    // 学生用户数量统计
    Integer studentUserCountStatistic();

    // 全站学习时长统计
    Integer overallStudyTimeStatistic();

    // 个人学习时长统计
    Integer personalStudyTimeStatistic(Integer id);

    // 全站成绩平均统计
    BigDecimal overallScoreAverageStatistic();

    // 个人成绩平均统计
    BigDecimal personalScoreAverageStatistic(Integer id);

    // 全站成绩排行统计
    Map<String, BigDecimal> overallScoreRankStatistic();

    // 个人成绩排行统计
    Map<String, BigDecimal> personalScoreRankStatistic(Integer id);

    // 全站成绩分段统计
    List<Integer> overallScoreSectionalStatistic();

    // 个人成绩分段统计
    List<Integer> personalScoreSectionalStatistic(Integer id);
}