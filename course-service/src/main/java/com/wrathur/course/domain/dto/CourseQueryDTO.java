package com.wrathur.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "课程分页查询条件")
public class CourseQueryDTO {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("审核状态")
    private Integer reviewStatus;
    @ApiModelProperty("学科")
    private Integer subjectId;
    @ApiModelProperty("类型")
    private Integer typeId;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}