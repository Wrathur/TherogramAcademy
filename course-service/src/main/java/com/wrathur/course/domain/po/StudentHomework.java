package com.wrathur.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_homework")
public class StudentHomework {
    private Integer studentId;
    private Integer homeworkId;
    private String attachment;
    private String reviewStatus; //ENUM: UNSUBMITTED/PENDING/APPROVED/REJECTED
    private BigDecimal score;
    private String rejectedReason;
    @TableField("is_deleted")
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime submitTime;
    private LocalDateTime evaluateTime;
    private LocalDateTime deleteTime;
}