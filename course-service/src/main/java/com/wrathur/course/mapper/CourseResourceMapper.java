package com.wrathur.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wrathur.course.domain.po.CourseResource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {
}