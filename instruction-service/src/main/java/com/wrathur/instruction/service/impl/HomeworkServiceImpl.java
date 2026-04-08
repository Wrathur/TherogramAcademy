package com.wrathur.instruction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wrathur.api.client.CourseServiceClient;
import com.wrathur.common.config.StorageProperties;
import com.wrathur.common.utils.FileStorageUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final CourseServiceClient courseServiceClient;
    private final StorageProperties storageProperties;

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
        if (studentIds != null && !studentIds.isEmpty()) {
            studentHomeworkMapper.insertBatch(studentIds.stream()
                    .map(studentId -> {
                        StudentHomework studentHomework = new StudentHomework();
                        studentHomework.setStudentId(studentId);
                        studentHomework.setHomeworkId(homework.getId());
                        studentHomework.setReviewStatus("UNSUBMITTED");
                        studentHomework.setIsDeleted(false);
                        studentHomework.setCreateTime(LocalDateTime.now());
                        studentHomework.setUpdateTime(LocalDateTime.now());
                        return studentHomework;
                    }).collect(Collectors.toList()));
        }
    }

    // 修改作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyHomework(HomeworkDTO homeworkDTO) {
        Homework homework = homeworkMapper.selectById(homeworkDTO.getId());
        BeanUtils.copyProperties(homeworkDTO, homework);
        homework.setUpdateTime(LocalDateTime.now());
        homeworkMapper.update(homework,
                new LambdaUpdateWrapper<Homework>()
                        .eq(Homework::getId, homeworkDTO.getId()));
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
        // 构建查询条件，从作业表获取该教师创建的作业ID列表
        LambdaQueryWrapper<Homework> pageWrapper = new LambdaQueryWrapper<>();
        pageWrapper.eq(Homework::getCourseId, homeworkQueryDTO.getCourseId());

        // 模糊查询名称
        if (homeworkQueryDTO.getName() != null && !homeworkQueryDTO.getName().isEmpty()) {
            pageWrapper.like(Homework::getName, homeworkQueryDTO.getName());
        }
        // 精确查询类型
        if (homeworkQueryDTO.getType() != null) {
            pageWrapper.eq(Homework::getType, homeworkQueryDTO.getType());
        }
        // 精确查询删除状态
        if (homeworkQueryDTO.getIsDeleted() != null && homeworkQueryDTO.getIsDeleted()) {
            pageWrapper.eq(Homework::getIsDeleted, false);
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

        // 排序
        if (homeworkQueryDTO.getSortType() != null && homeworkQueryDTO.getIsAsc() != null) {
            if (homeworkQueryDTO.getIsAsc()) {
                switch (homeworkQueryDTO.getSortType()) {
                    case 0:
                        pageWrapper.orderByAsc(Homework::getSubmitCount);
                        break;
                    case 1:
                        pageWrapper.orderByAsc(Homework::getDeadline);
                        break;
                    case 2:
                        pageWrapper.orderByAsc(Homework::getCreateTime);
                        break;
                    default:
                        pageWrapper.orderByAsc(Homework::getId);
                        break;
                }
            } else {
                switch (homeworkQueryDTO.getSortType()) {
                    case 0:
                        pageWrapper.orderByDesc(Homework::getSubmitCount);
                        break;
                    case 1:
                        pageWrapper.orderByDesc(Homework::getDeadline);
                        break;
                    case 2:
                        pageWrapper.orderByDesc(Homework::getCreateTime);
                        break;
                    default:
                        pageWrapper.orderByDesc(Homework::getId);
                        break;
                }
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
    public IPage<HomeworkVO> getStudentHomeworkPages(StudentHomeworkQueryDTO studentHomeworkQueryDTO) {
        // 构建分页对象
        Page<HomeworkVO> page = new Page<>(studentHomeworkQueryDTO.getPageNum(), studentHomeworkQueryDTO.getPageSize());

        // 执行分页查询
        IPage<HomeworkVO> studentHomeworkPage = homeworkMapper.selectStudentHomeworkPage(page, studentHomeworkQueryDTO.getStudentId(), studentHomeworkQueryDTO);

        // 转换为VO
        studentHomeworkPage.getRecords().forEach(homeworkVO -> {
            StudentHomework studentHomework = studentHomeworkMapper.selectOne(
                    new LambdaQueryWrapper<StudentHomework>()
                            .eq(StudentHomework::getStudentId, studentHomeworkQueryDTO.getStudentId())
                            .eq(StudentHomework::getHomeworkId, homeworkVO.getId()));
            homeworkVO.setStudentHomeworkAttachment(studentHomework.getAttachment());
            homeworkVO.setReviewStatus(studentHomework.getReviewStatus());
            homeworkVO.setScore(studentHomework.getScore());
            homeworkVO.setRejectedReason(studentHomework.getRejectedReason());
            homeworkVO.setStudentHomeworkCreateTime(studentHomework.getCreateTime());
            homeworkVO.setStudentHomeworkUpdateTime(studentHomework.getUpdateTime());
            homeworkVO.setStudentHomeworkSubmitTime(studentHomework.getSubmitTime());
            homeworkVO.setStudentHomeworkEvaluateTime(studentHomework.getEvaluateTime());
        });

        // 构建返回的分页VO
        return studentHomeworkPage;
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
        StudentHomework studentHomework = studentHomeworkMapper.selectOne(
                new LambdaQueryWrapper<StudentHomework>()
                        .eq(StudentHomework::getStudentId, studentId)
                        .eq(StudentHomework::getHomeworkId, homeworkId)
                        .eq(StudentHomework::getIsDeleted, false));
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
    public void submitHomework(StudentHomeworkDTO studentHomeworkDTO) {
        // 构建更新条件
        LambdaUpdateWrapper<StudentHomework> submitWrapper = new LambdaUpdateWrapper<>();
        submitWrapper.eq(StudentHomework::getStudentId, studentHomeworkDTO.getStudentId())
                .eq(StudentHomework::getHomeworkId, studentHomeworkDTO.getHomeworkId())
                .eq(StudentHomework::getIsDeleted, false);

        // 设置审核状态和时间
        submitWrapper.set(StudentHomework::getAttachment, studentHomeworkDTO.getAttachment());
        submitWrapper.set(StudentHomework::getReviewStatus, "PENDING");
        submitWrapper.set(StudentHomework::getUpdateTime, LocalDateTime.now());
        submitWrapper.set(StudentHomework::getSubmitTime, LocalDateTime.now());

        // 执行更新
        studentHomeworkMapper.update(null, submitWrapper);
    }

    // 评定作业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateHomework(StudentHomeworkDTO studentHomeworkDTO) {
        // 构建更新条件
        LambdaUpdateWrapper<StudentHomework> evaluateWrapper = new LambdaUpdateWrapper<>();
        evaluateWrapper.eq(StudentHomework::getStudentId, studentHomeworkDTO.getStudentId())
                .eq(StudentHomework::getHomeworkId, studentHomeworkDTO.getHomeworkId())
                .eq(StudentHomework::getIsDeleted, false);

        // 设置审核状态和时间
        evaluateWrapper.set(StudentHomework::getReviewStatus, studentHomeworkDTO.getReviewStatus());
        evaluateWrapper.set(StudentHomework::getUpdateTime, LocalDateTime.now());
        evaluateWrapper.set(StudentHomework::getEvaluateTime, LocalDateTime.now());

        // 设置分数和拒绝原因（审核通过时为 null）
        if ("APPROVED".equals(studentHomeworkDTO.getReviewStatus())) {
            evaluateWrapper.set(StudentHomework::getScore, studentHomeworkDTO.getScore());
            evaluateWrapper.set(StudentHomework::getRejectedReason, null);
        } else if ("REJECTED".equals(studentHomeworkDTO.getReviewStatus())) {
            evaluateWrapper.set(StudentHomework::getRejectedReason, studentHomeworkDTO.getRejectedReason());
        }

        // 执行更新
        studentHomeworkMapper.update(null, evaluateWrapper);
    }

    // 提醒作业
    @Override
    public List<HomeworkVO> remindHomework() {
        LambdaQueryWrapper<StudentHomework> queryWrapper = new LambdaQueryWrapper<StudentHomework>()
                .eq(StudentHomework::getIsDeleted, false);

        queryWrapper.eq(StudentHomework::getReviewStatus, "UNSUBMITTED");

//        queryWrapper.orderByDesc(StudentHomework::);

        List<StudentHomework> studentHomeworks = studentHomeworkMapper.selectList(queryWrapper);

        // 特殊属性需要额外赋值
        List<HomeworkVO> studentHomeworkVOS = new ArrayList<>();
        studentHomeworks.forEach(studentHomework -> {
            HomeworkVO homeworkVO = new HomeworkVO();
            homeworkVO.setId(studentHomework.getHomeworkId());
            homeworkVO.setName(homeworkMapper.selectById(studentHomework.getHomeworkId()).getName());
            studentHomeworkVOS.add(homeworkVO);
        });
        return studentHomeworkVOS;
    }

    // 上传作业附件
    @Override
    public void uploadHomeworkAttachment(Integer id, MultipartFile file) throws IOException {
        Homework homework = homeworkMapper.selectById(id);
        if (homework.getAttachment() != null && !homework.getAttachment().isEmpty()) {
            FileStorageUtils.deleteFile(storageProperties.getRootPath() + storageProperties.getHomeworkPath(), String.valueOf(id), homework.getAttachment());
        }
        FileStorageUtils.saveFile(storageProperties.getRootPath() + storageProperties.getHomeworkPath() + "/" + id, file);
        homeworkMapper.update(null,
                new LambdaUpdateWrapper<Homework>()
                        .eq(Homework::getId, id)
                        .set(Homework::getAttachment, file.getOriginalFilename()));
    }

    // 上传学生作业附件
    @Override
    public void uploadStudentHomeworkAttachment(Integer studentId, Integer homeworkId, MultipartFile file) throws IOException {
        StudentHomework studentHomework = studentHomeworkMapper.selectOne(
                new LambdaQueryWrapper<StudentHomework>()
                        .eq(StudentHomework::getStudentId, studentId)
                        .eq(StudentHomework::getHomeworkId, homeworkId)
                        .eq(StudentHomework::getIsDeleted, false));
        if (studentHomework.getAttachment() != null && !studentHomework.getAttachment().isEmpty()) {
            FileStorageUtils.deleteFile(storageProperties.getRootPath() + storageProperties.getStudentHomeworkPath(), studentId + "/" + homeworkId, studentHomework.getAttachment());
        }
        FileStorageUtils.saveFile(storageProperties.getRootPath() + storageProperties.getStudentHomeworkPath() + "/" + studentId + "/" + homeworkId, file);
        studentHomeworkMapper.update(null,
                new LambdaUpdateWrapper<StudentHomework>()
                        .eq(StudentHomework::getStudentId, studentId)
                        .eq(StudentHomework::getHomeworkId, homeworkId)
                        .set(StudentHomework::getAttachment, file.getOriginalFilename()));

    }

    // 通过课程获取所有未删除的作业
    @Override
    public List<Integer> getHomeworkIdsByCourseId(Integer id) {
        return baseMapper.selectObjs(new LambdaQueryWrapper<Homework>()
                        .eq(Homework::getCourseId, id)
                        .eq(Homework::getIsDeleted, false)
                        .select(Homework::getId)).stream()
                .map(obj -> (Integer) obj)
                .collect(Collectors.toList());
    }

    // 通过课程ID列表获取所有未删除的作业
    @Override
    public List<Integer> getHomeworkIdsByCourseIds(List<Integer> ids) {
        return homeworkMapper.selectHomeworkIdsByCourseIds(ids);
    }
}