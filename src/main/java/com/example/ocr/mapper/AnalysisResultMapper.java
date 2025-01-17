package com.example.ocr.mapper;

import com.example.ocr.entity.AnalysisResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnalysisResultMapper {
    int insert(AnalysisResult record);
    int deleteByRecordId(Long recordId);
    AnalysisResult selectByRecordId(Long recordId);
} 