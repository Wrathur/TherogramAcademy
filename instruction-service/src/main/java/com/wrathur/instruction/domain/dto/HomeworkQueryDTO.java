package com.wrathur.instruction.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "作业分页查询条件")
public class HomeworkQueryDTO {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型")
    private Integer type;
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
    @ApiModelProperty("是否按提交人数升序")
    private Boolean submitCountAsc;
    @ApiModelProperty("是否按截至时间升序")
    private Boolean deadlineAsc;
    @ApiModelProperty("是否按创建时间升序")
    private Boolean createTimeAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}