package com.wrathur.statistic.service;


import java.math.BigDecimal;
import java.util.List;

public interface ICourseStatisticService {
    // 课程数量统计（全站/个人）
    public Integer courseCountStatistic();

    // 选课人数统计（全站/个人）
    public Integer totalSelectCountStatistic();

    // 课程完成率统计（全站/个人）
    public BigDecimal courseCompletionRateStatistic();

    // 课程完成率排行（全站/个人）
    public List<String> courseCompletionRankStatistic();
}