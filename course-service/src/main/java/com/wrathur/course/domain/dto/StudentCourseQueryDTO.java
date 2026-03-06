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
    @ApiModelProperty("是否按选课人数升序（课程表字段）")
    private Boolean selectCountAsc;
    @ApiModelProperty("是否按进度升序（关联表字段）")
    private Boolean progressAsc;
    @ApiModelProperty("是否按学习时间升序（关联表字段）")
    private Boolean studyTimeAsc;
    @ApiModelProperty("是否按分数升序（关联表字段）")
    private Boolean scoreAsc;
    @ApiModelProperty("是否按创建时间升序（课程表字段）")
    private Boolean createTimeAsc;
    @ApiModelProperty("是否按选课时间升序（关联表字段）")
    private Boolean selectTimeAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}