package com.wrathur.instruction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.dto.HomeworkQueryDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkQueryDTO;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import com.wrathur.instruction.domain.vo.StudentHomeworkVO;

import java.math.BigDecimal;
import java.util.List;

public interface IHomeworkService {
    // 创建作业
    public void createHomework(HomeworkDTO homeworkDTO);

    // 修改作业
    public void modifyHomework(Integer id, HomeworkDTO homeworkDTO);

    // 删除作业
    public void deleteHomework(Integer id);

    // 获取作业分页
    public IPage<HomeworkVO> getHomeworkPages(Integer id, HomeworkQueryDTO homeworkQueryDTO);

    // 获取学生作业分页
    public IPage<HomeworkVO> getStudentHomeworkPages(Integer id, StudentHomeworkQueryDTO studentHomeworkQueryDTO);

    // 获取作业详情
    public HomeworkVO getHomeworkDetail(Integer id);

    // 获取学生作业详情
    public StudentHomeworkVO getStudentHomeworkDetail(Integer studentId, Integer homeworkId);

    // 提交作业
    public void submitHomework(StudentHomeworkDTO studentHomeworkDTO);

    // 评定作业
    public void evaluateHomework(StudentHomeworkDTO studentHomeworkDTO);

    // 通过课程获取所有未删除的作业
    public List<Integer> getHomeworkIdsByCourseId(Integer id);

    // 通过课程ID列表获取所有未删除的作业
    public List<Integer> getHomeworkIdsByCourseIds(List<Integer> ids);
}