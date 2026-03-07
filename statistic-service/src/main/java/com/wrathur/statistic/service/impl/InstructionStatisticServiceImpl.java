package com.wrathur.statistic.service.impl;

import com.wrathur.statistic.service.IInstructionStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructionStatisticServiceImpl implements IInstructionStatisticService {

    @Override
    public Integer teacherUserCountStatistic() {
        return 0;
    }

    @Override
    public Integer studentUserCountStatistic() {
        return 0;
    }

    @Override
    public Integer overallStudyTimeStatistic() {
        return 0;
    }

    @Override
    public Integer personalStudyTimeStatistic(Integer id) {
        return 0;
    }

    @Override
    public BigDecimal overallScoreAverageStatistic() {
        return null;
    }

    @Override
    public BigDecimal personalScoreAverageStatistic(Integer id) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> overallScoreRankStatistic() {
        return Map.of();
    }

    @Override
    public Map<String, BigDecimal> personalScoreRankStatistic(Integer id) {
        return Map.of();
    }

    @Override
    public List<Integer> overallScoreSectionalStatistic() {
        return List.of();
    }

    @Override
    public List<Integer> personalScoreSectionalStatistic(Integer id) {
        return List.of();
    }
}