package com.wrathur.instruction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wrathur.instruction.domain.po.Homework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {
    // 通过课程获取所有未删除的作业
    @Select("select id from homework where course_id = #{id} and is_deleted = false")
    List<Integer> selectHomeworkIdsByCourseId(Integer id);

    // 通过课程ID列表获取所有未删除的作业
    List<Integer> selectHomeworkIdsByCourseIds(List<Integer> ids);
}