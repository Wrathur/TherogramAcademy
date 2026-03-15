package com.wrathur.statistic.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICourseStatisticService {
    // 全站课程数量统计
    public Integer overallCourseCountStatistic();

    // 个人课程数量统计
    public Integer personalCourseCountStatistic(Integer id);

    // 全站选课人数统计
    public Integer overallSelectCountStatistic();

    // 个人选课人数统计
    public Integer personalSelectCountStatistic(Integer id);

    // 全站课程完成率平均统计
    public BigDecimal overallCourseCompletionRateAverageStatistic();

    // 个人课程完成率平均统计
    public BigDecimal personalCourseCompletionRateAverageStatistic(Integer id);

    // 全站课程完成率排行统计
    public Map<String, BigDecimal> overallCourseCompletionRateRankStatistic();

    // 个人课程完成率排行统计
    public Map<String, BigDecimal> personalCourseCompletionRateRankStatistic(Integer id);

    // 全站课程完成率分段统计
    public List<Integer> overallCourseCompletionRateSectionalStatistic();

    // 个人课程完成率分段统计
    public List<Integer> personalCourseCompletionRateSectionalStatistic(Integer id);
}