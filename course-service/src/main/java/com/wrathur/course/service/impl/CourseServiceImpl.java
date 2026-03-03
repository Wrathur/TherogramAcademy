package com.wrathur.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.po.Course;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.mapper.CourseMapper;
import com.wrathur.course.service.ICourseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CourseMapper courseMapper;

    // 创建课程
    @Override
    @Transactional
    public void createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
    }

    // 修改课程
    @Override
    @Transactional
    public void modifyCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setUpdateTime(LocalDateTime.now());

        // 更新审核状态时，需要额外处理
        if (courseDTO.getReviewStatus() != null) {
            UpdateWrapper<Course> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", courseDTO.getId())
                    .set("review_status", courseDTO.getReviewStatus());
            courseMapper.update(course, updateWrapper);
        }
    }

    // 删除课程
    @Override
    public void deleteCourse(Integer id) {
        courseMapper.deleteById(id);
    }

    // 获取课程详情
    @Override
    public CourseVO getCourseDetail(Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);

        //特殊属性需要额外赋值
        vo.setReviewStatus(course.getReviewStatus());
        vo.setCreateTime(course.getCreateTime());
        vo.setUpdateTime(course.getUpdateTime());
        return vo;
    }

    // 获取课程列表
    @Override
    public IPage<CourseVO> getAllCourses(CourseQueryDTO courseQueryDTO) {
        // 构建分页对象
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

        // 模糊查询课程名称
        if (courseQueryDTO.getName() != null && !courseQueryDTO.getName().isEmpty()) {
            queryWrapper.like(Course::getName, courseQueryDTO.getName());
        }
        // 精确查询学科
        if (courseQueryDTO.getSubjectId() != null) {
            queryWrapper.eq(Course::getSubjectId, courseQueryDTO.getSubjectId());
        }
        // 精确查询类型
        if (courseQueryDTO.getTypeId() != null) {
            queryWrapper.eq(Course::getTypeId, courseQueryDTO.getTypeId());
        }

        // 执行分页查询
        IPage<Course> coursePage = courseMapper.selectPage(page, queryWrapper);

        // 转换为VO
        List<CourseVO> vos = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO vo = new CourseVO();
                    BeanUtils.copyProperties(course, vo);
                    vo.setCreateTime(course.getCreateTime());
                    vo.setUpdateTime(course.getUpdateTime());
                    return vo;
                })
                .collect(Collectors.toList());

        // 构建返回的分页VO
        IPage<CourseVO> resultPage = new Page();
        resultPage.setRecords(vos);
        resultPage.setTotal(coursePage.getTotal());
        resultPage.setPages(coursePage.getPages());
        resultPage.setCurrent(coursePage.getCurrent());
        resultPage.setSize(coursePage.getSize());
        return resultPage;
    }

    // 评定课程
    @Override
    public void evaluateCourse(CourseDTO courseDTO) {

    }
}