package com.wrathur.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wrathur.statistic.domain.po.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    List<Map<String, BigDecimal>> selectOverallScoreRank();

    Map<String, Object> selectOverallScoreSectional();
}