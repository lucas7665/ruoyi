package com.example.ocr.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UploadRecord {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadTime;
    private String uploadUser;
    private Integer status = 1;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 