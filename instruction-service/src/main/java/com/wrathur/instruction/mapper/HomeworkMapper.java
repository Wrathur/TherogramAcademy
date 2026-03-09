package com.wrathur.instruction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wrathur.instruction.domain.dto.StudentHomeworkQueryDTO;
import com.wrathur.instruction.domain.po.Homework;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {
    // 通过课程ID列表获取所有未删除的作业
    List<Integer> selectHomeworkIdsByCourseIds(List<Integer> ids);

    @Select("<script>" +
            "SELECT h.*, sh.review_status, sh.score, sh.submit_time " +
            "FROM homework h " +
            "INNER JOIN student_homework sh ON h.id = sh.homework_id " +
            "WHERE sh.student_id = #{studentId} " +
            "AND h.is_deleted = 0 " +
            "AND sh.is_deleted = 0 " +
            // 作业表字段条件
            "<if test='dto.name != null and dto.name != \"\"'> AND h.name LIKE CONCAT('%', #{dto.name}, '%')</if>" +
            "<if test='dto.type != null'> AND h.type = #{dto.type}</if>" +
            "<if test='dto.startDeadline != null'> AND h.deadline >= #{dto.startDeadline}</if>" +
            "<if test='dto.endDeadline != null'> AND h.deadline &lt;= #{dto.endDeadline}</if>" +
            "<if test='dto.startCreateTime != null'> AND h.create_time >= #{dto.startCreateTime}</if>" +
            "<if test='dto.endCreateTime != null'> AND h.create_time &lt;= #{dto.endCreateTime}</if>" +
            // 关联表字段条件
            "<if test='dto.reviewStatus != null'> AND sh.review_status = #{dto.reviewStatus}</if>" +
            "<if test='dto.startScore != null'> AND sh.score >= #{dto.startScore}</if>" +
            "<if test='dto.endScore != null'> AND sh.score &lt;= #{dto.endScore}</if>" +
            "<if test='dto.startSubmitTime != null'> AND sh.submit_time >= #{dto.startSubmitTime}</if>" +
            "<if test='dto.endSubmitTime != null'> AND sh.submit_time &lt;= #{dto.endSubmitTime}</if>" +
            // 排序
            "<choose>" +
            "   <when test='dto.sortType != null'>" +
            "       ORDER BY " +
            "       <choose>" +
            "           <when test='dto.sortType == 0'>h.deadline</when>" +
            "           <when test='dto.sortType == 1'>h.create_time</when>" +
            "           <when test='dto.sortType == 2'>sh.score</when>" +
            "           <when test='dto.sortType == 3'>sh.submit_time</when>" +
            "           <otherwise>h.id</otherwise>" +
            "       </choose>" +
            "       <if test='dto.isAsc != null and !dto.isAsc'> DESC</if>" +
            "       <if test='dto.isAsc == null or dto.isAsc'> ASC</if>" +
            "   </when>" +
            "   <otherwise>ORDER BY h.id ASC</otherwise>" +
            "</choose>" +
            "</script>")
    IPage<HomeworkVO> selectStudentHomeworkPage(Page<?> page, @Param("studentId") Integer studentId, @Param("dto") StudentHomeworkQueryDTO dto);
}