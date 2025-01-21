package com.example.ocr.mapper;

import com.example.ocr.entity.NursingOcrResult;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NursingOcrResultMapper {
    
    @Insert("INSERT INTO nursing_ocr_result (record_id, ocr_text, create_time) " +
            "VALUES (#{recordId}, #{ocrText}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NursingOcrResult result);
    
    @Select("SELECT * FROM nursing_ocr_result WHERE record_id = #{recordId}")
    NursingOcrResult selectByRecordId(Long recordId);
} 