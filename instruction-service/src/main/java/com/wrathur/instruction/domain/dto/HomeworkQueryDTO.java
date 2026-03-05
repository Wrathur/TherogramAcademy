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
    @ApiModelProperty("批阅状态")
    private Integer reviewStatus;
    @ApiModelProperty("分数")
    private Integer score;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}