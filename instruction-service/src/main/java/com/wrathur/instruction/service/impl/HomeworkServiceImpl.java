package com.wrathur.instruction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wrathur.api.client.CourseServiceClient;
import com.wrathur.instruction.domain.dto.HomeworkDTO;
import com.wrathur.instruction.domain.dto.HomeworkQueryDTO;
import com.wrathur.instruction.domain.dto.StudentHomeworkDTO;
import com.wrathur.instruction.domain.po.Homework;
import com.wrathur.instruction.domain.po.StudentHomework;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import com.wrathur.instruction.mapper.HomeworkMapper;
import com.wrathur.instruction.mapper.StudentHomeworkMapper;
import com.wrathur.instruction.service.IHomeworkService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements IHomeworkService {

    private final HomeworkMapper homeworkMapper;
    private final StudentHomeworkMapper studentHomeworkMapper;

    @Setter
    private CourseServiceClient courseServiceClient;

    // 创建作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createHomework(HomeworkDTO homeworkDTO) {
        Homework homework = new Homework();
        BeanUtils.copyProperties(homeworkDTO, homework);
        homework.setIsDeleted(false);
        homework.setCreateTime(LocalDateTime.now());
        homework.setUpdateTime(LocalDateTime.now());
        homeworkMapper.insert(homework);

        // 查询选修了该课程的所有学生，将其添加到学生作业表，远程调用课程服务获取该课程所有未退选的学生
        List<Integer> studentIds = courseServiceClient.getStudentIdsByCourseId(homeworkDTO.getCourseId());

        // 为每个学生创建学生作业关联表项
        studentHomeworkMapper.insertBatch(studentIds.stream()
                .map(studentId -> {
                    StudentHomework studentHomework = new StudentHomework();
                    studentHomework.setStudentId(studentId);
                    studentHomework.setHomeworkId(homeworkDTO.getCourseId());
                    studentHomework.setReviewStatus("UNSUBMITTED");
                    studentHomework.setIsDeleted(false);
                    studentHomework.setCreateTime(LocalDateTime.now());
                    studentHomework.setUpdateTime(LocalDateTime.now());
                    return studentHomework;
                }).collect(Collectors.toList()));
    }

    // 修改作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyHomework(Integer id, HomeworkDTO homeworkDTO) {
        Homework homework = homeworkMapper.selectById(id);
        BeanUtils.copyProperties(homeworkDTO, homework);
        homework.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<Homework> modifyWrapper = new UpdateWrapper<>();
        modifyWrapper.eq("id", id);
        homeworkMapper.update(homework, modifyWrapper);
    }

    // 删除作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHomework(Integer id) {
        // 查询该作业，将其从作业表逻辑删除
        homeworkMapper.update(null,
                new LambdaUpdateWrapper<Homework>()
                        .eq(Homework::getId, id)
                        .set(Homework::getIsDeleted, true)
                        .set(Homework::getDeleteTime, LocalDateTime.now()));

        // 查询该作业的所有学生，将其从学生作业表逻辑删除
        studentHomeworkMapper.update(null,
                new LambdaUpdateWrapper<StudentHomework>()
                        .eq(StudentHomework::getHomeworkId, id)
                        .set(StudentHomework::getIsDeleted, true)
                        .set(StudentHomework::getDeleteTime, LocalDateTime.now()));
    }

    // 获取作业分页
    @Override
    public IPage<HomeworkVO> getHomeworkPages(HomeworkQueryDTO homeworkQueryDTO) {
        // 构建分页对象
        Page<Homework> page = new Page<>(homeworkQueryDTO.getPageNum(), homeworkQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Homework> pageWrapper = new LambdaQueryWrapper<>();

        // 模糊查询名称
        if (homeworkQueryDTO.getName() != null && !homeworkQueryDTO.getName().isEmpty()) {
            pageWrapper.like(Homework::getName, homeworkQueryDTO.getName());
        }
        // 精确查询类型
        if (homeworkQueryDTO.getType() != null) {
            pageWrapper.eq(Homework::getType, homeworkQueryDTO.getType());
        }
        // 精确查询批阅状态
        if (homeworkQueryDTO.getReviewStatus() != null) {
            pageWrapper.eq(Homework::getReviewStatus, homeworkQueryDTO.getReviewStatus());
        }
        // 精确查询分数
        if (homeworkQueryDTO.getScore() != null) {
            pageWrapper.eq(Homework::getScore, homeworkQueryDTO.getScore());
        }

        // 执行分页查询
        IPage<Homework> homeworkPage = homeworkMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<HomeworkVO> homeworkVOS = homeworkPage.getRecords().stream()
                .map(this::convertHomeworkToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(homeworkPage, homeworkVOS);
    }

    //转化VO
    private HomeworkVO convertHomeworkToVO(Homework homework) {
        HomeworkVO homeworkVO = new HomeworkVO();
        BeanUtils.copyProperties(homework, homeworkVO);
        homeworkVO.setDeadline(homework.getDeadline());
        homeworkVO.setCreateTime(homework.getCreateTime());
        homeworkVO.setUpdateTime(homework.getUpdateTime());
        homeworkVO.setDeleteTime(homework.getDeleteTime());
        return homeworkVO;
    }

    //转化分页结果
    private IPage<HomeworkVO> convertPageResult(IPage<Homework> homeworkPage, List<HomeworkVO> homeworkVOS) {
        IPage<HomeworkVO> resultPage = new Page<>();
        resultPage.setRecords(homeworkVOS);
        resultPage.setTotal(homeworkPage.getTotal());
        resultPage.setPages(homeworkPage.getPages());
        resultPage.setCurrent(homeworkPage.getCurrent());
        resultPage.setSize(homeworkPage.getSize());
        return resultPage;
    }

    // 获取作业详情
    @Override
    public HomeworkVO getHomeworkDetail(Integer id) {
        Homework homework = homeworkMapper.selectById(id);
        HomeworkVO homeworkVO = new HomeworkVO();
        BeanUtils.copyProperties(homework, homeworkVO);

        //特殊属性需要额外赋值
        homeworkVO.setDeadline(homework.getDeadline());
        homeworkVO.setCreateTime(homework.getCreateTime());
        homeworkVO.setUpdateTime(homework.getUpdateTime());
        homeworkVO.setDeleteTime(homework.getDeleteTime());
        return homeworkVO;
    }

    // 提交作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitHomework(String attachment, StudentHomeworkDTO studentHomeworkDTO) {
        StudentHomework studentHomework = new StudentHomework();
        studentHomework.setAttachment(attachment);
        studentHomework.setReviewStatus("PENDING");
        studentHomework.setUpdateTime(LocalDateTime.now());
    }

    // 评定作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateHomework(BigDecimal score, StudentHomeworkDTO studentHomeworkDTO) {
        StudentHomework studentHomework = new StudentHomework();
        BeanUtils.copyProperties(studentHomeworkDTO, studentHomework);
        studentHomework.setReviewStatus("PENDING");
        studentHomework.setScore(score);
        studentHomework.setUpdateTime(LocalDateTime.now());

        MPJLambdaWrapper<StudentHomework> evaluateWrapper = new MPJLambdaWrapper<>();
        evaluateWrapper.eq(StudentHomework::getStudentId, studentHomeworkDTO.getStudentId())
                .eq(StudentHomework::getHomeworkId, studentHomeworkDTO.getHomeworkId());
        studentHomeworkMapper.update(studentHomework, evaluateWrapper);
    }

    // 通过课程获取所有未删除的作业
    @Override
    public List<Integer> getHomeworkIdsByCourseId(Integer id) {
        return homeworkMapper.selectHomeworkIdsByCourseId(id);
    }

    // 通过课程ID列表获取所有未删除的作业
    @Override
    public List<Integer> getHomeworkIdsByCourseIds(List<Integer> ids) {
        return homeworkMapper.selectHomeworkIdsByCourseIds(ids);
    }
}