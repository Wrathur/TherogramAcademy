package com.wrathur.instruction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.instruction.domain.dto.CourseResourceDTO;
import com.wrathur.instruction.domain.dto.CourseResourceQueryDTO;
import com.wrathur.instruction.domain.vo.CourseResourceVO;

public interface ICourseResourceService {
    // 创建教学资源
    public void createCourseResource(CourseResourceDTO courseResourceDTO);

    // 修改教学资源
    public void modifyCourseResource(Integer id, CourseResourceDTO courseResourceDTO);

    // 删除教学资源
    public void deleteCourseResource(Integer id);

    // 获取教学资源分页
    public IPage<CourseResourceVO> getCourseResourcePages(CourseResourceQueryDTO courseResourceQueryDTO);

    // 获取教学资源详情
    public CourseResourceVO getCourseResourceDetail(Integer id);
}