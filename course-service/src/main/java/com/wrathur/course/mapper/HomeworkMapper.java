package com.wrathur.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wrathur.course.domain.po.Homework;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {
}