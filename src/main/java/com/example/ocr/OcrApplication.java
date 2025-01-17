package com.example.ocr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.example.ocr.mapper")
@EnableConfigurationProperties
public class OcrApplication {
    public static void main(String[] args) {
        SpringApplication.run(OcrApplication.class, args);
    }
} 