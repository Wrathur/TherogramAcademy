package com.wrathur.course.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wrathur.course.domain.po.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentCourseMapper extends MPJBaseMapper<StudentCourse> {
    // 通过课程获取所有未退选的学生
    @Select("select id from student_course where course_id = #{id} and is_deleted = false")
    List<Integer> selectStudentIdsByCourseId(Integer id);
}