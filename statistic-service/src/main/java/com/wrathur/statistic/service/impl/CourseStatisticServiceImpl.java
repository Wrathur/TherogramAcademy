package com.wrathur.statistic.service.impl;

import com.wrathur.statistic.service.ICourseStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseStatisticServiceImpl implements ICourseStatisticService {

    @Override
    public Integer overallCourseCountStatistic() {
        return 0;
    }

    @Override
    public Integer personalCourseCountStatistic(Integer id) {
        return 0;
    }

    @Override
    public Integer overallTotalSelectCountStatistic() {
        return 0;
    }

    @Override
    public Integer personalTotalSelectCountStatistic(Integer id) {
        return 0;
    }

    @Override
    public BigDecimal overallCourseCompletionRateAverageStatistic() {
        return null;
    }

    @Override
    public BigDecimal personalCourseCompletionRateAverageStatistic(Integer id) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> overallCourseCompletionRateRankStatistic() {
        return Map.of();
    }

    @Override
    public Map<String, BigDecimal> personalCourseCompletionRateRankStatistic(Integer id) {
        return Map.of();
    }

    @Override
    public List<Integer> overallCourseCompletionRateSectionalStatistic() {
        return List.of();
    }

    @Override
    public List<Integer> personalCourseCompletionRateSectionalStatistic(Integer id) {
        return List.of();
    }
}