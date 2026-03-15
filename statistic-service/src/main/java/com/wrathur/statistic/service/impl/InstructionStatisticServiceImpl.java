package com.wrathur.statistic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wrathur.statistic.domain.po.StudentCourse;
import com.wrathur.statistic.domain.po.User;
import com.wrathur.statistic.mapper.StudentCourseMapper;
import com.wrathur.statistic.mapper.UserMapper;
import com.wrathur.statistic.service.IInstructionStatisticService;
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
public class InstructionStatisticServiceImpl implements IInstructionStatisticService {

    private final UserMapper userMapper;
    private final StudentCourseMapper studentCourseMapper;

    @Override
    public Integer teacherUserCountStatistic() {
        return Math.toIntExact(userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRoleType, "TEACHER")
                        .eq(User::getIsDeleted, false)
        ));
    }

    @Override
    public Integer studentUserCountStatistic() {
        return Math.toIntExact(userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRoleType, "STUDENT")
                        .eq(User::getIsDeleted, false)
        ));
    }

    @Override
    public Integer overallStudyTimeStatistic() {
        List<StudentCourse> list = studentCourseMapper.selectList(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getIsDeleted, false)
                        .isNotNull(StudentCourse::getStudyTime)
        );
        return list.stream().mapToInt(StudentCourse::getStudyTime).sum();
    }

    @Override
    public Integer personalStudyTimeStatistic(Integer userId) {
        List<StudentCourse> list = studentCourseMapper.selectList(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, userId)
                        .eq(StudentCourse::getIsDeleted, false)
                        .isNotNull(StudentCourse::getStudyTime)
        );
        return list.stream().mapToInt(StudentCourse::getStudyTime).sum();
    }

    @Override
    public BigDecimal overallScoreAverageStatistic() {
        List<StudentCourse> list = studentCourseMapper.selectList(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getIsDeleted, false)
                        .isNotNull(StudentCourse::getScore)
        );
        if (list.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = new BigDecimal(0);
        for (StudentCourse studentCourse : list) {
            sum = sum.add(studentCourse.getScore());
        }
        BigDecimal avg = sum.divide(BigDecimal.valueOf(list.size()), RoundingMode.HALF_UP);
        return avg.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal personalScoreAverageStatistic(Integer userId) {
        List<StudentCourse> list = studentCourseMapper.selectList(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, userId)
                        .eq(StudentCourse::getIsDeleted, false)
                        .isNotNull(StudentCourse::getScore)
        );
        if (list.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = new BigDecimal(0);
        for (StudentCourse studentCourse : list) {
            sum = sum.add(studentCourse.getScore());
        }
        BigDecimal avg = sum.divide(BigDecimal.valueOf(list.size()), RoundingMode.HALF_UP);
        return avg.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, BigDecimal> overallScoreRankStatistic() {
        List<Map<String, BigDecimal>> list = userMapper.selectOverallScoreRank();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map<String, BigDecimal> item : list) {
            result.put(String.valueOf(item.get("username")), item.get("avgScore"));
        }
        return result;
    }

    @Override
    public Map<String, BigDecimal> personalScoreRankStatistic(Integer userId) {
        List<Map<String, BigDecimal>> list = studentCourseMapper.selectPersonalScoreRank(userId);
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map<String, BigDecimal> item : list) {
            result.put(String.valueOf(item.get("courseName")), item.get("avgScore"));
        }
        return result;
    }

    @Override
    public List<Integer> overallScoreSectionalStatistic() {
        Map<String, Object> map = userMapper.selectOverallScoreSectional();
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
    public List<Integer> personalScoreSectionalStatistic(Integer userId) {
        Map<String, Object> map = studentCourseMapper.selectPersonalScoreSectional(userId);
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