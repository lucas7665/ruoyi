package com.example.ocr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.ocr.config.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@MapperScan("com.example.ocr.mapper")
@EnableConfigurationProperties
public class OcrApplication {
    
    @Autowired
    private FileStorageConfig fileStorageConfig;
    
    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(fileStorageConfig.getPath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }
    
    public static void main(String[] args) {
        SpringApplication.run(OcrApplication.class, args);
    }
} 