package com.wrathur.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_course")
public class StudentCourse {
    private Integer studentId;
    private Integer courseId;
    private Integer progress;
    private BigDecimal score;
    @TableField("is_deleted")
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime selectTime;
    private LocalDateTime evaluateTime;
    private LocalDateTime deleteTime;
}