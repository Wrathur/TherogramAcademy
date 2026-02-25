package com.wrathur.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("address")
public class StudentCourse {
    private Integer studentId;
    private Integer courseId;
    private Integer progress;
    private BigDecimal score;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}