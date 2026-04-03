package com.wrathur.instruction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.dto.HomeworkQueryDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkQueryDTO;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import com.wrathur.instruction.domain.vo.StudentHomeworkVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IHomeworkService {
    // 创建作业
    void createHomework(HomeworkDTO homeworkDTO);

    // 修改作业
    void modifyHomework(HomeworkDTO homeworkDTO);

    // 删除作业
    void deleteHomework(Integer id);

    // 获取作业分页
    IPage<HomeworkVO> getHomeworkPages(HomeworkQueryDTO homeworkQueryDTO);

    // 获取学生作业分页
    IPage<HomeworkVO> getStudentHomeworkPages(StudentHomeworkQueryDTO studentHomeworkQueryDTO);

    // 获取作业详情
    HomeworkVO getHomeworkDetail(Integer id);

    // 获取学生作业详情
    StudentHomeworkVO getStudentHomeworkDetail(Integer studentId, Integer homeworkId);

    // 提交作业
    void submitHomework(StudentHomeworkDTO studentHomeworkDTO);

    // 评定作业
    void evaluateHomework(StudentHomeworkDTO studentHomeworkDTO);

    // 提醒作业
    List<HomeworkVO> remindHomework();

    // 上传作业附件
    void uploadHomeworkAttachment(Integer id, MultipartFile file) throws IOException;

    // 上传学生作业附件
    void uploadStudentHomeworkAttachment(Integer studentId, Integer homeworkId, MultipartFile file) throws IOException;

    // 通过课程获取所有未删除的作业
    List<Integer> getHomeworkIdsByCourseId(Integer id);

    // 通过课程ID列表获取所有未删除的作业
    List<Integer> getHomeworkIdsByCourseIds(List<Integer> ids);
}