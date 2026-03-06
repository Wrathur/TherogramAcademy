package com.wrathur.instruction.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
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
import com.wrathur.instruction.domain.dto.StudentHomeworkQueryDTO;
import com.wrathur.instruction.domain.po.Homework;
import com.wrathur.instruction.domain.po.StudentHomework;
import com.wrathur.instruction.domain.vo.HomeworkVO;
import com.wrathur.instruction.domain.vo.StudentHomeworkVO;
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
    public IPage<HomeworkVO> getHomeworkPages(Integer id, HomeworkQueryDTO homeworkQueryDTO) {
        // 构建查询条件，从作业表获取该教师创建的作业ID列表
        LambdaQueryWrapper<Homework> pageWrapper = new LambdaQueryWrapper<>();
        pageWrapper.eq(Homework::getCourseId, id);

        // 模糊查询名称
        if (homeworkQueryDTO.getName() != null && !homeworkQueryDTO.getName().isEmpty()) {
            pageWrapper.like(Homework::getName, homeworkQueryDTO.getName());
        }
        // 精确查询类型
        if (homeworkQueryDTO.getType() != null) {
            pageWrapper.eq(Homework::getType, homeworkQueryDTO.getType());
        }

        // 范围查询提交人数
        if (homeworkQueryDTO.getStartSubmitCount() != null) {
            pageWrapper.ge(Homework::getSubmitCount, homeworkQueryDTO.getStartSubmitCount());
        }
        if (homeworkQueryDTO.getEndSubmitCount() != null) {
            pageWrapper.le(Homework::getSubmitCount, homeworkQueryDTO.getEndSubmitCount());
        }
        // 范围查询截至时间
        if (homeworkQueryDTO.getStartDeadline() != null) {
            pageWrapper.ge(Homework::getDeadline, homeworkQueryDTO.getStartDeadline());
        }
        if (homeworkQueryDTO.getEndDeadline() != null) {
            pageWrapper.le(Homework::getDeadline, homeworkQueryDTO.getEndDeadline());
        }
        // 范围查询创建时间
        if (homeworkQueryDTO.getStartCreateTime() != null) {
            pageWrapper.ge(Homework::getCreateTime, homeworkQueryDTO.getStartCreateTime());
        }
        if (homeworkQueryDTO.getEndCreateTime() != null) {
            pageWrapper.le(Homework::getCreateTime, homeworkQueryDTO.getEndCreateTime());
        }

        // 按ID排序
        pageWrapper.orderByDesc(Homework::getId);
        // 按提交人数排序
        if (homeworkQueryDTO.getSubmitCountAsc() != null) {
            if (homeworkQueryDTO.getSubmitCountAsc()) {
                pageWrapper.orderByAsc(Homework::getSubmitCount);
            } else {
                pageWrapper.orderByDesc(Homework::getSubmitCount);
            }
        }
        // 按截至时间排序
        if (homeworkQueryDTO.getDeadlineAsc() != null) {
            if (homeworkQueryDTO.getDeadlineAsc()) {
                pageWrapper.orderByAsc(Homework::getDeadline);
            } else {
                pageWrapper.orderByDesc(Homework::getDeadline);
            }
        }
        // 按创建时间排序
        if (homeworkQueryDTO.getCreateTimeAsc() != null) {
            if (homeworkQueryDTO.getCreateTimeAsc()) {
                pageWrapper.orderByAsc(Homework::getCreateTime);
            } else {
                pageWrapper.orderByDesc(Homework::getCreateTime);
            }
        }

        // 构建分页对象
        Page<Homework> page = new Page<>(homeworkQueryDTO.getPageNum(), homeworkQueryDTO.getPageSize());

        // 执行分页查询
        IPage<Homework> homeworkPage = homeworkMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<HomeworkVO> homeworkVOS = homeworkPage.getRecords().stream()
                .map(this::convertHomeworkToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(homeworkPage, homeworkVOS);
    }

    //获取学生作业分页
    @Override
    public IPage<HomeworkVO> getStudentHomeworkPages(Integer id, StudentHomeworkQueryDTO studentHomeworkQueryDTO) {
        // 从学生作业关联表获取作业ID列表
        LambdaQueryWrapper<StudentHomework> studentHomeworkPageWrapper = new LambdaQueryWrapper<>();
        studentHomeworkPageWrapper.eq(StudentHomework::getStudentId, id);

        // 精确查询批阅状态（关联表字段）
        if (studentHomeworkQueryDTO.getReviewStatus() != null) {
            studentHomeworkPageWrapper.ge(StudentHomework::getReviewStatus, studentHomeworkQueryDTO.getReviewStatus());
        }

        // 范围查询分数（关联表字段）
        if (studentHomeworkQueryDTO.getStartScore() != null) {
            studentHomeworkPageWrapper.ge(StudentHomework::getScore, studentHomeworkQueryDTO.getStartScore());
        }
        if (studentHomeworkQueryDTO.getEndScore() != null) {
            studentHomeworkPageWrapper.le(StudentHomework::getScore, studentHomeworkQueryDTO.getEndScore());
        }

        // 范围查询提交时间（关联表字段）
        if (studentHomeworkQueryDTO.getStartSubmitTime() != null) {
            studentHomeworkPageWrapper.ge(StudentHomework::getSubmitTime, studentHomeworkQueryDTO.getStartSubmitTime());
        }
        if (studentHomeworkQueryDTO.getStartSubmitTime() != null) {
            studentHomeworkPageWrapper.le(StudentHomework::getSubmitTime, studentHomeworkQueryDTO.getStartSubmitTime());
        }

        // 按分数排序
        if (studentHomeworkQueryDTO.getScoreAsc() != null) {
            if (studentHomeworkQueryDTO.getScoreAsc()) {
                studentHomeworkPageWrapper.orderByAsc(StudentHomework::getScore);
            } else {
                studentHomeworkPageWrapper.orderByDesc(StudentHomework::getScore);
            }
        }
        // 按提交时间排序
        if (studentHomeworkQueryDTO.getSubmitTimeAsc() != null) {
            if (studentHomeworkQueryDTO.getSubmitTimeAsc()) {
                studentHomeworkPageWrapper.orderByAsc(StudentHomework::getSubmitTime);
            } else {
                studentHomeworkPageWrapper.orderByDesc(StudentHomework::getSubmitTime);
            }
        }

        List<Object> homeworkIds = studentHomeworkMapper.selectObjs(studentHomeworkPageWrapper);

        // 如果没有符合条件的作业
        if (homeworkIds == null || homeworkIds.isEmpty()) {
            return new Page<>();
        }

        // 从作业表查询符合条件的作业
        LambdaQueryWrapper<Homework> homeworkPageWrapper = new LambdaQueryWrapper<>();
        homeworkPageWrapper.in(Homework::getId, homeworkIds);

        // 模糊查询名称（作业表字段）
        if (studentHomeworkQueryDTO.getName() != null && StringUtils.isNotBlank(studentHomeworkQueryDTO.getName())) {
            homeworkPageWrapper.like(Homework::getName, "%" + studentHomeworkQueryDTO.getName() + "%");
        }
        // 精确查询类型（作业表字段）
        if (studentHomeworkQueryDTO.getType() != null) {
            homeworkPageWrapper.eq(Homework::getType, studentHomeworkQueryDTO.getType());
        }

        // 范围查询截至时间（作业表字段）
        if (studentHomeworkQueryDTO.getStartDeadline() != null) {
            homeworkPageWrapper.ge(Homework::getDeadline, studentHomeworkQueryDTO.getStartDeadline());
        }
        if (studentHomeworkQueryDTO.getEndDeadline() != null) {
            homeworkPageWrapper.le(Homework::getDeadline, studentHomeworkQueryDTO.getEndDeadline());
        }
        // 范围查询创建时间（作业表字段）
        if (studentHomeworkQueryDTO.getStartCreateTime() != null) {
            homeworkPageWrapper.ge(Homework::getCreateTime, studentHomeworkQueryDTO.getStartCreateTime());
        }
        if (studentHomeworkQueryDTO.getEndCreateTime() != null) {
            homeworkPageWrapper.le(Homework::getCreateTime, studentHomeworkQueryDTO.getEndCreateTime());
        }

        // 按ID排序（作业表字段）
        homeworkPageWrapper.orderByDesc(Homework::getId);
        // 按截至时间排序（作业表字段）
        if (studentHomeworkQueryDTO.getDeadlineAsc() != null) {
            if (studentHomeworkQueryDTO.getDeadlineAsc()) {
                homeworkPageWrapper.orderByAsc(Homework::getDeadline);
            } else {
                homeworkPageWrapper.orderByDesc(Homework::getDeadline);
            }
        }
        // 按创建时间排序（作业表字段）
        if (studentHomeworkQueryDTO.getCreateTimeAsc() != null) {
            if (studentHomeworkQueryDTO.getCreateTimeAsc()) {
                homeworkPageWrapper.orderByAsc(Homework::getCreateTime);
            } else {
                homeworkPageWrapper.orderByDesc(Homework::getCreateTime);
            }
        }

        // 构建分页对象
        Page<Homework> page = new Page<>(studentHomeworkQueryDTO.getPageNum(), studentHomeworkQueryDTO.getPageSize());

        // 执行分页查询
        IPage<Homework> homeworkPage = homeworkMapper.selectPage(page, homeworkPageWrapper);

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

    // 获取学生作业详情
    @Override
    public StudentHomeworkVO getStudentHomeworkDetail(Integer studentId, Integer homeworkId) {
        LambdaQueryWrapper<StudentHomework> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentHomework::getStudentId, studentId)
                .eq(StudentHomework::getHomeworkId, homeworkId)
                .eq(StudentHomework::getIsDeleted, false);
        StudentHomework studentHomework = studentHomeworkMapper.selectOne(queryWrapper);
        StudentHomeworkVO studentHomeworkVO = new StudentHomeworkVO();
        BeanUtils.copyProperties(studentHomework, studentHomeworkVO);

        // 特殊属性需要额外赋值
        studentHomeworkVO.setScore(studentHomework.getScore());
        studentHomeworkVO.setCreateTime(studentHomework.getCreateTime());
        studentHomeworkVO.setUpdateTime(studentHomework.getUpdateTime());
        studentHomeworkVO.setSubmitTime(studentHomework.getSubmitTime());
        studentHomeworkVO.setEvaluateTime(studentHomework.getEvaluateTime());
        studentHomeworkVO.setDeleteTime(studentHomework.getDeleteTime());
        return studentHomeworkVO;
    }

    // 提交作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitHomework(String attachment, StudentHomeworkDTO studentHomeworkDTO) {
        LambdaQueryWrapper<StudentHomework> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentHomework::getStudentId, studentHomeworkDTO.getStudentId())
                .eq(StudentHomework::getHomeworkId, studentHomeworkDTO.getHomeworkId())
                .eq(StudentHomework::getIsDeleted, false);
        StudentHomework studentHomework = studentHomeworkMapper.selectOne(queryWrapper);

        studentHomework.setAttachment(attachment);
        studentHomework.setReviewStatus("PENDING");
        studentHomework.setUpdateTime(LocalDateTime.now());
        studentHomework.setSubmitTime(LocalDateTime.now());
    }

    // 评定作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateHomework(BigDecimal score, StudentHomeworkDTO studentHomeworkDTO) {
        LambdaQueryWrapper<StudentHomework> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentHomework::getStudentId, studentHomeworkDTO.getStudentId())
                .eq(StudentHomework::getHomeworkId, studentHomeworkDTO.getHomeworkId())
                .eq(StudentHomework::getIsDeleted, false);
        StudentHomework studentHomework = studentHomeworkMapper.selectOne(queryWrapper);

        if (studentHomeworkDTO.getReviewStatus().equals("APPROVED")) {
            studentHomework.setReviewStatus("APPROVED");
            studentHomework.setScore(score);
            studentHomework.setUpdateTime(LocalDateTime.now());
            studentHomework.setEvaluateTime(LocalDateTime.now());
        } else if (studentHomeworkDTO.getReviewStatus().equals("REJECTED")) {
            studentHomework.setReviewStatus("REJECTED");
            studentHomework.setRejectedReason(studentHomeworkDTO.getRejectedReason());
            studentHomework.setUpdateTime(LocalDateTime.now());
            studentHomework.setEvaluateTime(LocalDateTime.now());
        }

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