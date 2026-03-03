package com.wrathur.instruction.service;

import com.wrathur.instruction.domain.dto.CourseResourceDTO;
import com.wrathur.instruction.domain.vo.CourseResourceVO;

import java.util.List;

public interface IInstructionResourceService {
    // 创建教学资源
    public void createCourseResource(CourseResourceDTO courseResourceDTO);

    // 修改教学资源
    public void modifyCourseResource(CourseResourceDTO courseResourceDTO);

    // 删除教学资源
    public void deleteCourseResource(CourseResourceDTO courseResourceDTO);

    // 获取教学资源列表
    public List<CourseResourceVO> getAllCourseResources();

    // 获取教学资源详情
    public CourseResourceVO getCourseResourceDetail(Integer courseResourceId);
}