package com.example.ocr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.File;

@Slf4j
@Configuration
public class UploadConfig implements WebMvcConfigurer {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            File uploadDir = new File(uploadPath);
            log.info("初始化上传目录: {}", uploadDir.getAbsolutePath());
            
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("创建上传目录失败: {}", uploadPath);
                    throw new RuntimeException("无法创建上传目录: " + uploadPath);
                }
                log.info("成功创建上传目录");
            }
            
            if (!uploadDir.canWrite()) {
                log.error("上传目录没有写入权限: {}", uploadPath);
                throw new RuntimeException("上传目录没有写入权限: " + uploadPath);
            }
            
            log.info("上传目录初始化成功");
        } catch (Exception e) {
            log.error("初始化上传目录失败", e);
            throw new RuntimeException("初始化上传目录失败", e);
        }
    }
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
