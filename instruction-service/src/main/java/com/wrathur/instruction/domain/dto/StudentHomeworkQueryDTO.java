package com.wrathur.instruction.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "作业分页查询条件")
public class StudentHomeworkQueryDTO {
    @ApiModelProperty("名称（作业表字段）")
    private String name;
    @ApiModelProperty("类型（作业表字段）")
    private Integer type;
    @ApiModelProperty("批阅状态（关联表字段）")
    private Integer reviewStatus;
    @ApiModelProperty("起始分数（关联表字段）")
    private BigDecimal startScore;
    @ApiModelProperty("结束分数（关联表字段）")
    private BigDecimal endScore;
    @ApiModelProperty("起始截至时间（作业表字段）")
    private LocalDateTime startDeadline;
    @ApiModelProperty("结束截至时间（作业表字段）")
    private LocalDateTime endDeadline;
    @ApiModelProperty("起始创建时间（作业表字段）")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间（作业表字段）")
    private LocalDateTime endCreateTime;
    @ApiModelProperty("起始提交时间（关联表字段）")
    private LocalDateTime startSubmitTime;
    @ApiModelProperty("结束提交时间（关联表字段）")
    private LocalDateTime endSubmitTime;
    @ApiModelProperty("是否按分数升序（关联表字段）")
    private Boolean scoreAsc;
    @ApiModelProperty("是否按截至时间升序（作业表字段）")
    private Boolean deadlineAsc;
    @ApiModelProperty("是否按创建时间升序（作业表字段）")
    private Boolean createTimeAsc;
    @ApiModelProperty("是否按提交时间升序（关联表字段）")
    private Boolean submitTimeAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}