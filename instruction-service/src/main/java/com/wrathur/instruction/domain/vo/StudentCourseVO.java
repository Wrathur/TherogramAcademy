package com.wrathur.instruction.domain.vo;

import lombok.Data;

@Data
public class StudentCourseVO {
    private Integer studentId;
    private Integer courseId;
    private String progress;
    private String score;

    public void setProgress(Integer progress) {
        this.progress = progress != null ? progress + "%" : "0%";
    }

    public void setScore(Double score) {
        this.score = score != null ? String.format("%.1f", score) : "未评分";
    }
}