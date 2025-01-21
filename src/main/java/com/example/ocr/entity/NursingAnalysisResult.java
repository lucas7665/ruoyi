package com.example.ocr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("nursing_analysis_result")
public class NursingAnalysisResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private String analysisText;
    private String modelAnswer;
    private Integer abnormalCount;
    private LocalDateTime createTime;
} 