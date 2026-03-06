package com.wrathur.instruction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wrathur.instruction.domain.dto.CourseResourceDTO;
import com.wrathur.instruction.domain.dto.CourseResourceQueryDTO;
import com.wrathur.instruction.domain.po.CourseResource;
import com.wrathur.instruction.domain.vo.CourseResourceVO;
import com.wrathur.instruction.mapper.CourseResourceMapper;
import com.wrathur.instruction.service.ICourseResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseResourceServiceImpl extends ServiceImpl<CourseResourceMapper, CourseResource> implements ICourseResourceService {

    private final CourseResourceMapper courseResourceMapper;

    // 创建教学资源
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCourseResource(CourseResourceDTO courseResourceDTO) {
        CourseResource courseResource = new CourseResource();
        BeanUtils.copyProperties(courseResourceDTO, courseResource);
        courseResource.setIsDeleted(false);
        courseResource.setCreateTime(LocalDateTime.now());
        courseResource.setUpdateTime(LocalDateTime.now());
        courseResourceMapper.insert(courseResource);
    }

    // 修改教学资源
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCourseResource(Integer id, CourseResourceDTO courseResourceDTO) {
        CourseResource courseResource = courseResourceMapper.selectById(id);
        BeanUtils.copyProperties(courseResourceDTO, courseResource);
        courseResource.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<CourseResource> modifyWrapper = new UpdateWrapper<>();
        modifyWrapper.eq("id", id);
        courseResourceMapper.update(courseResource, modifyWrapper);
    }

    // 删除教学资源
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourseResource(Integer id) {
        courseResourceMapper.update(null,
                new LambdaUpdateWrapper<CourseResource>()
                        .eq(CourseResource::getId, id)
                        .set(CourseResource::getIsDeleted, true)
                        .set(CourseResource::getDeleteTime, LocalDateTime.now()));
    }

    // 获取教学资源分页
    @Override
    public IPage<CourseResourceVO> getCourseResourcePages(CourseResourceQueryDTO courseResourceQueryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<CourseResource> pageWrapper = new LambdaQueryWrapper<>();

        // 过滤已删除课程资源
        pageWrapper.eq(CourseResource::getIsDeleted, false);

        // 模糊查询名称
        if (courseResourceQueryDTO.getName() != null && !courseResourceQueryDTO.getName().isEmpty()) {
            pageWrapper.like(CourseResource::getName, courseResourceQueryDTO.getName());
        }
        // 精确查询类型
        if (courseResourceQueryDTO.getResourceType() != null) {
            pageWrapper.eq(CourseResource::getResourceType, courseResourceQueryDTO.getResourceType());
        }

        // 范围查询查看次数
        if (courseResourceQueryDTO.getStartViewCount() != null) {
            pageWrapper.ge(CourseResource::getViewCount, courseResourceQueryDTO.getStartViewCount());
        }
        if (courseResourceQueryDTO.getEndViewCount() != null) {
            pageWrapper.le(CourseResource::getViewCount, courseResourceQueryDTO.getEndViewCount());
        }
        // 范围查询创建时间
        if (courseResourceQueryDTO.getStartCreateTime() != null) {
            pageWrapper.ge(CourseResource::getCreateTime, courseResourceQueryDTO.getStartCreateTime());
        }
        if (courseResourceQueryDTO.getEndCreateTime() != null) {
            pageWrapper.le(CourseResource::getCreateTime, courseResourceQueryDTO.getEndCreateTime());
        }

        // 按ID排序
        pageWrapper.orderByDesc(CourseResource::getId);
        // 按查看次数排序
        if (courseResourceQueryDTO.getViewCountAsc() != null) {
            if (courseResourceQueryDTO.getViewCountAsc()) {
                pageWrapper.orderByAsc(CourseResource::getViewCount);
            } else {
                pageWrapper.orderByDesc(CourseResource::getViewCount);
            }
        }
        // 按创建时间排序
        if (courseResourceQueryDTO.getCreateTimeAsc() != null) {
            if (courseResourceQueryDTO.getCreateTimeAsc()) {
                pageWrapper.orderByAsc(CourseResource::getCreateTime);
            } else {
                pageWrapper.orderByDesc(CourseResource::getCreateTime);
            }
        }

        // 构建分页对象
        Page<CourseResource> page = new Page<>(courseResourceQueryDTO.getPageNum(), courseResourceQueryDTO.getPageSize());

        // 执行分页查询
        IPage<CourseResource> courseResourcePage = courseResourceMapper.selectPage(page, pageWrapper);

        // 转换为VO
        List<CourseResourceVO> courseVOS = courseResourcePage.getRecords().stream()
                .map(this::convertCourseResourceToVO)
                .collect(Collectors.toList());

        // 构建返回的分页VO
        return convertPageResult(courseResourcePage, courseVOS);
    }

    //转化VO
    private CourseResourceVO convertCourseResourceToVO(CourseResource courseResource) {
        CourseResourceVO courseResourceVO = new CourseResourceVO();
        BeanUtils.copyProperties(courseResource, courseResourceVO);
        courseResourceVO.setCreateTime(courseResource.getCreateTime());
        courseResourceVO.setUpdateTime(courseResource.getUpdateTime());
        courseResourceVO.setDeleteTime(courseResource.getDeleteTime());
        return courseResourceVO;
    }

    //转化分页结果
    private IPage<CourseResourceVO> convertPageResult(IPage<CourseResource> courseResourcePage, List<CourseResourceVO> courseResourceVOS) {
        IPage<CourseResourceVO> resultPage = new Page<>();
        resultPage.setRecords(courseResourceVOS);
        resultPage.setTotal(courseResourcePage.getTotal());
        resultPage.setPages(courseResourcePage.getPages());
        resultPage.setCurrent(courseResourcePage.getCurrent());
        resultPage.setSize(courseResourcePage.getSize());
        return resultPage;
    }

    // 获取教学资源详情
    @Override
    public CourseResourceVO getCourseResourceDetail(Integer id) {
        CourseResource courseResource = courseResourceMapper.selectById(id);
        CourseResourceVO courseResourceVO = new CourseResourceVO();
        BeanUtils.copyProperties(courseResource, courseResourceVO);

        //特殊属性需要额外赋值
        courseResourceVO.setCreateTime(courseResource.getCreateTime());
        courseResourceVO.setUpdateTime(courseResource.getUpdateTime());
        courseResourceVO.setDeleteTime(courseResource.getDeleteTime());
        return courseResourceVO;
    }
}