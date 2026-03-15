package com.wrathur.statistic.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wrathur.statistic.domain.po.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface StudentCourseMapper extends MPJBaseMapper<StudentCourse> {
    List<Map<String, BigDecimal>> selectPersonalCompletionRank(@Param("userId") Integer userId);

    Map<String, Object> selectPersonalCompletionSectional(@Param("userId") Integer userId);

    List<Map<String, BigDecimal>> selectPersonalScoreRank(@Param("userId") Integer userId);

    Map<String, Object> selectPersonalScoreSectional(@Param("userId") Integer userId);
}