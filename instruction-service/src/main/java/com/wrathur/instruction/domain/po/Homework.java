package com.wrathur.instruction.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("homework")
public class Homework {
    private Integer id;
    private String name;
    private Integer type;
    private LocalDateTime deadline;
    private String content;
    private String attachment;
    private Integer submitCount;
    @TableField("is_deleted")
    private Boolean isDeleted;
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}