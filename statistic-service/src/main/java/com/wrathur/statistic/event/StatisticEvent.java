package com.wrathur.statistic.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticEvent implements Serializable {
    private String type; // 事件类型：COURSE_CREATED, COURSE_SELECTED, PROGRESS_UPDATED 等
    private Integer userId; // 相关用户ID（可为空）
    private Integer courseId; // 相关课程ID（可为空）
    private Integer homeworkId; // 相关作业ID（可为空）
    private Integer studyTime; // 学习时长增量
    private Integer score; // 分数
    private String userRole; // 用户角色：TEACHER/STUDENT
}