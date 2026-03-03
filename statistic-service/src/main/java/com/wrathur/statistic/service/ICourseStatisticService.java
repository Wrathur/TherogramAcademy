package com.wrathur.statistic.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICourseStatisticService {
    // 课程数量统计（全站/个人）
    public Integer courseCountStatistic();

    // 选课人数统计（全站/个人）
    public Integer totalSelectCountStatistic();

    // 课程完成率平均统计（全站/个人）
    public BigDecimal courseCompletionRateAverageStatistic();

    // 课程完成率排行统计（全站/个人）
    public Map<String, BigDecimal> courseCompletionRateRankStatistic();

    // 课程完成率分段统计（全站/个人）
    public List<Integer> courseCompletionRateSectionalStatistic();
}