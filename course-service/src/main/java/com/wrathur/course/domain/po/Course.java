package com.wrathur.course.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String profile;
    private String target;
    private String content;
    private String outline;
    private String reviewStatus; //ENUM: PENDING/APPROVED/REJECTED
    private Integer subjectId;
    private Integer typeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}