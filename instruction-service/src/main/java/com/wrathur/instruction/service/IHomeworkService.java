package com.wrathur.instruction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.dto.HomeworkQueryDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkDTO;
import com.wrathur.instruction.domain.vo.HomeworkVO;

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
    public IPage<HomeworkVO> getHomeworkPages(HomeworkQueryDTO homeworkQueryDTO);

    // 获取作业详情
    public HomeworkVO getHomeworkDetail(Integer id);

    // 提交作业
    public void submitHomework(String attachment, StudentHomeworkDTO studentHomeworkDTO);

    // 评定作业
    public void evaluateHomework(BigDecimal score, StudentHomeworkDTO studentHomeworkDTO);

    // 通过课程获取所有未删除的作业
    public List<Integer> getHomeworkIdsByCourseId(Integer id);
}