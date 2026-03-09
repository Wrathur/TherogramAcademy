package com.wrathur.course.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wrathur.course.domain.po.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentCourseMapper extends MPJBaseMapper<StudentCourse> {
}