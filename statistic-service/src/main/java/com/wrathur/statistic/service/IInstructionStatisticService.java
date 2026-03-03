package com.wrathur.statistic.service;


import java.math.BigDecimal;
import java.util.List;

public interface IInstructionStatisticService {
    // 用户数量统计（教师/学生）
    public Integer studentCountStatistic();

    // 学习时长统计（全站/个人）
    public Integer studyTimeStatistic();

    // 平均成绩统计（全站/个人）
    public BigDecimal averageScoreStatistic();

    // 各分段成绩统计（全站/个人）
    public List<Integer> sectionalScoreStatistic();
}