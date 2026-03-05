package com.wrathur.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wrathur.course.domain.po.StudentHomework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentHomeworkMapper extends BaseMapper<StudentHomework> {
    void insertBatch(@Param("list") List<StudentHomework> studentHomeworkList);
}