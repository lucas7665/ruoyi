package com.example.ocr.mapper;

import com.example.ocr.entity.NursingAnalysisResult;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NursingAnalysisResultMapper {
    
    @Insert("INSERT INTO nursing_analysis_result (record_id, analysis_text, model_answer, create_time) " +
            "VALUES (#{recordId}, #{analysisText}, #{modelAnswer}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NursingAnalysisResult result);
    
    @Select("SELECT * FROM nursing_analysis_result WHERE record_id = #{recordId}")
    NursingAnalysisResult selectByRecordId(Long recordId);
} 