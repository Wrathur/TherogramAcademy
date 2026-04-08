package com.wrathur.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileStorageUtils {

    /**
     * 保存文件
     */
    public static void saveFile(String baseDir, MultipartFile file) throws IOException {
        Path filePath = Paths.get(baseDir, file.getOriginalFilename());
        file.transferTo(filePath.toFile());
        log.info("文件保存成功: {}", filePath);
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String baseDir, String id, String relativePath) {
        Path filePath = Paths.get(baseDir, id, relativePath);
        File file = filePath.toFile();
        if (file.exists()) {
            if (file.delete()) {
                log.info("文件删除成功: {}", filePath);
            } else {
                log.warn("文件删除失败: {}", filePath);
            }
        } else {
            log.warn("文件不存在: {}", filePath);
        }
    }
}