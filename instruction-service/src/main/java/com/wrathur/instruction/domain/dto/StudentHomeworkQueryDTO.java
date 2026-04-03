package com.wrathur.instruction.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "作业分页查询条件")
public class StudentHomeworkQueryDTO {
    @ApiModelProperty("学生id")
    private Integer studentId;
    @ApiModelProperty("名称（作业表字段）")
    private String name;
    @ApiModelProperty("类型（作业表字段）")
    private String type;
    @ApiModelProperty("批阅状态（关联表字段）")
    private String reviewStatus;
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
    @ApiModelProperty("排序方式")
    private Integer sortType; // 0：截至时间（作业表字段） 1：创建时间（作业表字段） 2：分数（关联表字段） 3：提交时间（关联表字段）
    @ApiModelProperty("是否升序")
    private Boolean isAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}