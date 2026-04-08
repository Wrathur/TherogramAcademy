package com.wrathur.instruction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wrathur.instruction.domain.dto.CourseResourceDTO;
import com.wrathur.instruction.domain.dto.CourseResourceQueryDTO;
import com.wrathur.instruction.domain.vo.CourseResourceVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ICourseResourceService {
    // 创建教学资源
    CourseResourceVO createCourseResource(CourseResourceDTO courseResourceDTO);

    // 修改教学资源
    void modifyCourseResource(CourseResourceDTO courseResourceDTO);

    // 删除教学资源
    void deleteCourseResource(Integer id);

    // 获取教学资源分页
    IPage<CourseResourceVO> getCourseResourcePages(CourseResourceQueryDTO courseResourceQueryDTO);

    // 获取教学资源详情
    CourseResourceVO getCourseResourceDetail(Integer id);

    // 上传教学资源
    void uploadCourseResource(Integer id, MultipartFile file) throws IOException;

    // 通过课程获取所有未删除的教学资源
    List<Integer> getCourseResourceIdsByCourseId(Integer id);
}