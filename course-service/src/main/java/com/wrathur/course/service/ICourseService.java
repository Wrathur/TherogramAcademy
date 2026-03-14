package com.wrathur.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.po.StudentCourse;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.domain.vo.StudentCourseVO;

import java.math.BigDecimal;
import java.util.List;

public interface ICourseService extends IService<StudentCourse> {
    // 创建课程
    public void createCourse(CourseDTO courseDTO);

    // 修改课程
    public void modifyCourse(Integer id, CourseDTO courseDTO);

    // 删除课程
    public void deleteCourse(Integer id);

    // 获取课程分页
    public IPage<CourseVO> getCoursePages(CourseQueryDTO courseQueryDTO);

    // 获取创建课程分页
    public IPage<CourseVO> getCreateCoursePages(CourseQueryDTO courseQueryDTO);

    // 获取选修课程分页
    public IPage<CourseVO> getSelectCoursePages(StudentCourseQueryDTO studentCourseQueryDTO);

    // 获取课程详情
    public CourseVO getCourseDetail(Integer id);

    // 获取创建课程详情
    public CourseVO getCreateCourseDetail(Integer id);

    // 获取选修课程详情
    public StudentCourseVO getSelectCourseDetail(Integer id);

    // 审核课程
    public void reviewCourse(String reviewStatus, CourseDTO courseDTO);

    // 选修课程
    public void selectCourse(Integer id);

    // 退选课程
    void deselectCourse(Integer id);

    // 评定课程
    public void evaluateCourse(BigDecimal score, Integer studentId, Integer courseId);

    // 通过课程获取所有未退选的学生
    public List<Integer> getStudentIdsByCourseId(Integer id);
}