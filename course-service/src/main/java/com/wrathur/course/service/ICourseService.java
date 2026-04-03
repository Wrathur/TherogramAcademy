package com.wrathur.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wrathur.course.domain.dto.CourseDTO;
import com.wrathur.course.domain.dto.CourseQueryDTO;
import com.wrathur.course.domain.dto.StudentCourseDTO;
import com.wrathur.course.domain.dto.StudentCourseQueryDTO;
import com.wrathur.course.domain.po.StudentCourse;
import com.wrathur.course.domain.vo.CourseVO;
import com.wrathur.course.domain.vo.StudentCourseVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ICourseService extends IService<StudentCourse> {
    // 创建课程
    void createCourse(CourseDTO courseDTO);

    // 修改课程
    void modifyCourse(CourseDTO courseDTO);

    // 删除课程
    void deleteCourse(Integer id);

    // 获取课程分页
    IPage<CourseVO> getCoursePages(CourseQueryDTO courseQueryDTO);

    // 获取创建课程分页
    IPage<CourseVO> getCreateCoursePages(CourseQueryDTO courseQueryDTO);

    // 获取选修课程分页
    IPage<CourseVO> getSelectCoursePages(StudentCourseQueryDTO studentCourseQueryDTO);

    // 通过关键字获取搜索课程分页
    IPage<CourseVO> getSearchCoursePagesByKeyword(CourseQueryDTO courseQueryDTO);

    // 获取课程详情
    CourseVO getCourseDetail(Integer id);

    // 获取创建课程详情
    CourseVO getCreateCourseDetail(Integer id);

    // 获取选修课程详情
    StudentCourseVO getSelectCourseDetail(Integer id);

    // 审核课程
    void reviewCourse(CourseDTO courseDTO);

    // 选修课程
    void selectCourse(Integer id);

    // 退选课程
    void deselectCourse(Integer id);

    // 更新课程进度和学习时间
    void updateCourseProgressAndStudyTime(StudentCourseDTO studentCourseDTO);

    // 评定课程
    void evaluateCourse(StudentCourseDTO studentCourseDTO);

    // 推荐课程
    List<CourseVO> recommendCourse(Integer courseSubject, Integer courseType);

    // 上传课程封面
    void uploadCourseCover(Integer id, MultipartFile file) throws IOException;

    // 通过课程获取所有未退选的学生
    List<Integer> getStudentIdsByCourseId(Integer id);

    // 通过用户id获取该用户创建的课程
    List<CourseVO> getCreateCourseIdsByUserId(Integer id);

    // 通过用户id获取该用户选修的课程
    List<CourseVO> getSelectCourseIdsByUserId(Integer id);
}