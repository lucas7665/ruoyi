package com.example.ocr.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NursingAnalysisResult {
    private Long id;
    private Long recordId;
    private String analysisText;
    private String modelAnswer;
    private LocalDateTime createTime;
} 