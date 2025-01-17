package com.example.ocr.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OcrResult {
    private Long id;
    private Long recordId;
    private String ocrText;
    private LocalDateTime createTime;
}
