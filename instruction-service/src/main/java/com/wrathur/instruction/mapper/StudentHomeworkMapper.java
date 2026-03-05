package com.wrathur.instruction.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wrathur.instruction.domain.po.StudentHomework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentHomeworkMapper extends MPJBaseMapper<StudentHomework> {
    void insertBatch(@Param("list") List<StudentHomework> studentHomeworkList);
}