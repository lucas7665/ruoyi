package com.example.ocr.mapper;

import com.example.ocr.entity.NursingAnalysisResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NursingAnalysisResultMapper {
    
    int insert(NursingAnalysisResult result);
    
    NursingAnalysisResult selectByRecordId(Long recordId);
} 