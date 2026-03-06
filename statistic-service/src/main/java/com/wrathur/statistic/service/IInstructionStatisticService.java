package com.wrathur.statistic.service;


import com.wrathur.statistic.domain.dto.UserDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInstructionStatisticService {
    // 用户数量统计（教师/学生）
    public Integer studentCountStatistic(UserDTO userDTO);

    // 学习时长统计（全站/个人）
    public BigDecimal studyTimeStatistic(UserDTO userDTO);

    // 成绩平均统计（全站/个人）
    public BigDecimal scoreAverageStatistic(UserDTO userDTO);

    // 成绩排行统计（全站/个人）
    public Map<String, BigDecimal> scoreRankStatistic(UserDTO userDTO);

    // 成绩分段统计（全站/个人）
    public List<Integer> scoreSectionalStatistic(UserDTO userDTO);
}