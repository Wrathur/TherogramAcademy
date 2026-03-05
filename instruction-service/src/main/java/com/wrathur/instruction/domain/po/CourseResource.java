package com.wrathur.instruction.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_resource")
public class CourseResource {
    private Integer id;
    private String name;
    private String uri;
    private String resourceType; //VIDEO/MATERIAL/REFERENCE
    @TableField("is_deleted")
    private Boolean isDeleted;
    private Integer courseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}