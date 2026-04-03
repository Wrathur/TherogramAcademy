package com.wrathur.instruction.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "作业分页查询条件")
public class HomeworkQueryDTO {
    @ApiModelProperty("课程id")
    private Integer courseId;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("删除状态")
    private Boolean isDeleted;
    @ApiModelProperty("起始提交人数")
    private Integer startSubmitCount;
    @ApiModelProperty("结束提交人数")
    private Integer endSubmitCount;
    @ApiModelProperty("起始截至时间")
    private LocalDateTime startDeadline;
    @ApiModelProperty("结束截至时间")
    private LocalDateTime endDeadline;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    @ApiModelProperty("排序方式")
    private Integer sortType; // 0：提交人数 1：截至时间 2：创建时间
    @ApiModelProperty("是否升序")
    private Boolean isAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}