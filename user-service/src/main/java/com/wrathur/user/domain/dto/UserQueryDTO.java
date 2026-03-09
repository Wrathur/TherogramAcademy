package com.wrathur.user.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "用户分页查询条件")
public class UserQueryDTO {
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("类型")
    private String roleType;
    @ApiModelProperty("删除状态")
    private Boolean isDeleted;
    @ApiModelProperty("起始创建时间")
    private LocalDateTime startCreateTime;
    @ApiModelProperty("结束创建时间")
    private LocalDateTime endCreateTime;
    @ApiModelProperty("排序方式")
    private Integer sortType; //0：创建时间
    @ApiModelProperty("是否升序")
    private Boolean isAsc;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}