package com.wrathur.statistic.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInstructionStatisticService {
    // 用户数量统计（教师/学生）
    public Integer studentCountStatistic();

    // 学习时长统计（全站/个人）
    public BigDecimal studyTimeStatistic();

    // 成绩平均统计（全站/个人）
    public BigDecimal scoreAverageStatistic();

    // 成绩排行统计（全站/个人）
    public Map<String, BigDecimal> scoreRankStatistic();

    // 成绩分段统计（全站/个人）
    public List<Integer> scoreSectionalStatistic();
}