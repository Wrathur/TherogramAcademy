package com.wrathur.instruction.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "课程资源分页查询条件")
public class CourseResourceQueryDTO {
    @ApiModelProperty("课程id")
    private Integer courseId;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型")
    private Integer resourceType;
    @ApiModelProperty("起始查看次数")
    private Integer startViewCount;
    @ApiModelProperty("结束查看次数")
    private Integer endViewCount;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    @ApiModelProperty("排序方式")
    private Integer sortType; // 0：id 1：查看次数 2：创建时间
    @ApiModelProperty("是否升序")
    private Boolean isAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}