package com.wrathur.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "课程分页查询条件")
public class CourseQueryDTO {
    @ApiModelProperty("搜索关键字")
    private String name;
    @ApiModelProperty("学科")
    private Integer subjectId;
    @ApiModelProperty("类型")
    private Integer typeId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}