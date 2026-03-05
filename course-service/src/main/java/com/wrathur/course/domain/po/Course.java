package com.wrathur.course.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    private Integer id;
    private String name;
    private String profile;
    private String target;
    private String content;
    private String outline;
    private String reviewStatus; //ENUM: PENDING/APPROVED/REJECTED
    private String rejectedReason;
    @TableField("is_deleted")
    private Boolean isDeleted;
    private Integer subjectId;
    private Integer typeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime reviewTime;
    private LocalDateTime deleteTime;
}