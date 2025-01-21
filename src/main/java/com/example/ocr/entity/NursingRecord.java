package com.example.ocr.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NursingRecord {
    private Long id;
    private String fileName;        // 原始文件名
    private String storedFileName;  // 存储的文件名
    private Long fileSize;          // 文件大小(字节)
    private String fileType;        // 文件类型
    private Integer startPage;      // PDF开始页码
    private Integer endPage;        // PDF结束页码
    private Integer totalPages;     // PDF总页数
    private LocalDateTime uploadTime; // 上传时间
    private Integer status;         // 0-待识别，1-待分析，2-已分析，-1-已删除
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
} 