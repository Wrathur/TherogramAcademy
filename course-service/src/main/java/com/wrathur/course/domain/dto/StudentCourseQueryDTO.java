package com.wrathur.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "选修课程分页查询条件")
public class StudentCourseQueryDTO {
    @ApiModelProperty("名称（课程表字段）")
    private String name;
    @ApiModelProperty("学科（课程表字段）")
    private Integer subjectId;
    @ApiModelProperty("类型（课程表字段）")
    private Integer typeId;
    @ApiModelProperty("起始选课人数（课程表字段）")
    private Integer startSelectCount;
    @ApiModelProperty("结束选课人数（课程表字段）")
    private Integer endSelectCount;
    @ApiModelProperty("起始进度（关联表字段）")
    private Integer startProgress;
    @ApiModelProperty("结束进度（关联表字段）")
    private Integer endProgress;
    @ApiModelProperty("起始学习时间（关联表字段）")
    private Integer startStudyTime;
    @ApiModelProperty("结束学习时间（关联表字段）")
    private Integer endStudyTime;
    @ApiModelProperty("起始分数（关联表字段）")
    private BigDecimal startScore;
    @ApiModelProperty("结束分数（关联表字段）")
    private BigDecimal endScore;
    @ApiModelProperty("起始创建时间（课程表字段）")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间（课程表字段）")
    private LocalDateTime endCreateTime;
    @ApiModelProperty("起始选修时间（关联表字段）")
    private LocalDateTime startSelectTime;
    @ApiModelProperty("结束选修时间（关联表字段）")
    private LocalDateTime endSelectTime;
    @ApiModelProperty("排序方式")
    private Integer sortType; // 0：选课人数（课程表字段） 1：创建时间（课程表字段） 2：进度（关联表字段） 3：学习时间（关联表字段） 4：分数（关联表字段） 5：选课时间排序（关联表字段）
    @ApiModelProperty("是否升序")
    private Boolean isAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}