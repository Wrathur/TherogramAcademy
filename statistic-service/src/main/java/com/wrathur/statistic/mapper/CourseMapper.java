package com.wrathur.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wrathur.statistic.domain.po.Course;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    List<Map<String, BigDecimal>> selectOverallCourseCompletionRank();

    Map<String, Object> selectOverallCompletionSectional();
}