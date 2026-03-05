package com.wrathur.instruction.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "课程资源分页查询条件")
public class CourseResourceQueryDTO {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型")
    private Integer resourceType;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}