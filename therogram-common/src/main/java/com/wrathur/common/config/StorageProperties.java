package com.wrathur.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.storage")
public class StorageProperties {
    /**
     * 文件存储根路径
     */
    private String rootPath;

    /**
     * 课程模块存储子路径
     */
    private String coursePath;

    /**
     * 教学资源模块存储子路径
     */
    private String courseResourcePath;

    /**
     * 作业模块存储子路径
     */
    private String homeworkPath;

    /**
     * 学生作业模块存储子路径
     */
    private String studentHomeworkPath;

    /**
     * 用户头像模块存储子路径
     */
    private String userPath;
}