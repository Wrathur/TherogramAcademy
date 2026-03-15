package com.wrathur.statistic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wrathur.statistic.domain.po.Course;
import com.wrathur.statistic.domain.po.StudentCourse;
import com.wrathur.statistic.mapper.CourseMapper;
import com.wrathur.statistic.mapper.StudentCourseMapper;
import com.wrathur.statistic.service.ICourseStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseStatisticServiceImpl implements ICourseStatisticService {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;

    @Override
    public Integer overallCourseCountStatistic() {
        return Math.toIntExact(courseMapper.selectCount(
                new LambdaQueryWrapper<Course>().eq(Course::getIsDeleted, false)
        ));
    }

    @Override
    public Integer personalCourseCountStatistic(Integer userId) {
        return Math.toIntExact(courseMapper.selectCount(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getTeacherId, userId)
                        .eq(Course::getIsDeleted, false)
        ));
    }

    @Override
    public Integer overallSelectCountStatistic() {
        return Math.toIntExact(studentCourseMapper.selectCount(
                new LambdaQueryWrapper<StudentCourse>().eq(StudentCourse::getIsDeleted, false)
        ));
    }

    @Override
    public Integer personalSelectCountStatistic(Integer userId) {
        return Math.toIntExact(studentCourseMapper.selectCount(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, userId)
                        .eq(StudentCourse::getIsDeleted, false)
        ));
    }

    @Override
    public BigDecimal overallCourseCompletionRateAverageStatistic() {
        List<StudentCourse> list = studentCourseMapper.selectList(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getIsDeleted, false)
                        .isNotNull(StudentCourse::getProgress)
        );
        if (list.isEmpty()) return BigDecimal.ZERO;
        double sum = list.stream().mapToInt(StudentCourse::getProgress).sum();
        return BigDecimal.valueOf(sum / list.size()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal personalCourseCompletionRateAverageStatistic(Integer userId) {
        List<StudentCourse> list = studentCourseMapper.selectList(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, userId)
                        .eq(StudentCourse::getIsDeleted, false)
                        .isNotNull(StudentCourse::getProgress)
        );
        if (list.isEmpty()) return BigDecimal.ZERO;
        double sum = list.stream().mapToInt(StudentCourse::getProgress).sum();
        return BigDecimal.valueOf(sum / list.size()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, BigDecimal> overallCourseCompletionRateRankStatistic() {
        List<Map<String, BigDecimal>> list = courseMapper.selectOverallCourseCompletionRank();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map<String, BigDecimal> item : list) {
            result.put(String.valueOf(item.get("courseName")), item.get("avgProgress"));
        }
        return result;
    }

    @Override
    public Map<String, BigDecimal> personalCourseCompletionRateRankStatistic(Integer userId) {
        List<Map<String, BigDecimal>> list = studentCourseMapper.selectPersonalCompletionRank(userId);
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map<String, BigDecimal> item : list) {
            result.put(String.valueOf(item.get("courseName")), item.get("avgProgress"));
        }
        return result;
    }

    @Override
    public List<Integer> overallCourseCompletionRateSectionalStatistic() {
        Map<String, Object> map = courseMapper.selectOverallCompletionSectional();
        List<Integer> result = new ArrayList<>();
        if (map == null) {
            for (int i = 0; i < 5; i++) result.add(0);
        } else {
            result.add(((Number) map.get("section1")).intValue());
            result.add(((Number) map.get("section2")).intValue());
            result.add(((Number) map.get("section3")).intValue());
            result.add(((Number) map.get("section4")).intValue());
            result.add(((Number) map.get("section5")).intValue());
        }
        return result;
    }

    @Override
    public List<Integer> personalCourseCompletionRateSectionalStatistic(Integer userId) {
        Map<String, Object> map = studentCourseMapper.selectPersonalCompletionSectional(userId);
        List<Integer> result = new ArrayList<>();
        if (map == null) {
            for (int i = 0; i < 5; i++) result.add(0);
        } else {
            result.add(((Number) map.get("section1")).intValue());
            result.add(((Number) map.get("section2")).intValue());
            result.add(((Number) map.get("section3")).intValue());
            result.add(((Number) map.get("section4")).intValue());
            result.add(((Number) map.get("section5")).intValue());
        }
        return result;
    }
}