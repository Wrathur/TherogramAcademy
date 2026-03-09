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
    private String reviewStatus;
    @ApiModelProperty("学科")
    private Integer subjectId;
    @ApiModelProperty("类型")
    private Integer typeId;
    @ApiModelProperty("选修状态")
    private Boolean isSelected;
    @ApiModelProperty("删除状态")
    private Boolean isDeleted;
    @ApiModelProperty("起始选课人数")
    private Integer startSelectCount;
    @ApiModelProperty("结束选课人数")
    private Integer endSelectCount;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    @ApiModelProperty("排序方式")
    private Integer sortType; // 0：选课人数 1：创建时间
    @ApiModelProperty("是否升序")
    private Boolean isAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}