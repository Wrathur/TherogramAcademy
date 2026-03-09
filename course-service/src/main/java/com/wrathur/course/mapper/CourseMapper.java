package com.wrathur.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.po.Course;
import com.wrathur.course.domain.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    @Select("<script>" +
            "SELECT c.*, sc.progress, sc.study_time, sc.score, sc.select_time " +
            "FROM course c " +
            "INNER JOIN student_course sc ON c.id = sc.course_id " +
            "WHERE sc.student_id = #{studentId} " +
            "AND c.is_deleted = 0 " +
            "AND sc.is_deleted = 0 " +
            // 课程表字段条件
            "<if test='dto.name != null and dto.name != \"\"'> AND c.name LIKE CONCAT('%', #{dto.name}, '%')</if>" +
            "<if test='dto.subjectId != null'> AND c.subject_id = #{dto.subjectId}</if>" +
            "<if test='dto.typeId != null'> AND c.type_id = #{dto.typeId}</if>" +
            "<if test='dto.startSelectCount != null'> AND c.select_count >= #{dto.startSelectCount}</if>" +
            "<if test='dto.endSelectCount != null'> AND c.select_count &lt;= #{dto.endSelectCount}</if>" +
            "<if test='dto.startCreateTime != null'> AND c.create_time >= #{dto.startCreateTime}</if>" +
            "<if test='dto.endCreateTime != null'> AND c.create_time &lt;= #{dto.endCreateTime}</if>" +
            // 关联表字段条件
            "<if test='dto.startProgress != null'> AND sc.progress >= #{dto.startProgress}</if>" +
            "<if test='dto.endProgress != null'> AND sc.progress &lt;= #{dto.endProgress}</if>" +
            "<if test='dto.startStudyTime != null'> AND sc.study_time >= #{dto.startStudyTime}</if>" +
            "<if test='dto.endStudyTime != null'> AND sc.study_time &lt;= #{dto.endStudyTime}</if>" +
            "<if test='dto.startScore != null'> AND sc.score >= #{dto.startScore}</if>" +
            "<if test='dto.endScore != null'> AND sc.score &lt;= #{dto.endScore}</if>" +
            "<if test='dto.startSelectTime != null'> AND sc.select_time >= #{dto.startSelectTime}</if>" +
            "<if test='dto.endSelectTime != null'> AND sc.select_time &lt;= #{dto.endSelectTime}</if>" +
            // 排序
            "<choose>" +
            "   <when test='dto.sortType != null'>" +
            "       ORDER BY " +
            "       <choose>" +
            "           <when test='dto.sortType == 0'>c.select_count</when>" +
            "           <when test='dto.sortType == 1'>c.create_time</when>" +
            "           <when test='dto.sortType == 2'>sc.progress</when>" +
            "           <when test='dto.sortType == 3'>sc.study_time</when>" +
            "           <when test='dto.sortType == 4'>sc.score</when>" +
            "           <when test='dto.sortType == 5'>sc.select_time</when>" +
            "           <otherwise>c.id</otherwise>" +
            "       </choose>" +
            "       <if test='dto.isAsc != null and !dto.isAsc'> DESC</if>" +
            "       <if test='dto.isAsc == null or dto.isAsc'> ASC</if>" +
            "   </when>" +
            "   <otherwise>ORDER BY c.id ASC</otherwise>" +
            "</choose>" +
            "</script>")
    IPage<CourseVO> selectStudentCoursePage(Page<?> page, @Param("studentId") Integer studentId, @Param("dto") StudentCourseQueryDTO dto);
}