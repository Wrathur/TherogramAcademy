package com.wrathur.instruction.service;

import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.vo.HomeworkVO;

import java.math.BigDecimal;
import java.util.List;

public interface IHomeworkService {
    // 创建作业
    public void createHomework(HomeworkDTO homeworkDTO);

    // 修改作业
    public void modifyHomework(HomeworkDTO homeworkDTO);

    // 删除作业
    public void deleteHomework(HomeworkDTO homeworkDTO);

    // 获取作业列表
    public List<HomeworkVO> getAllHomework();

    // 获取作业详情
    public HomeworkVO getHomeworkDetail(Integer homeworkId);

    // 提交作业
    public void submitHomework(String homeworkUrl);

    // 评定作业
    public BigDecimal evaluateHomework(Integer homeworkId);
}