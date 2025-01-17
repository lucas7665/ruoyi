package com.example.ocr.mapper;

import com.example.ocr.entity.OcrResult;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OcrResultMapper {
    
    @Insert("INSERT INTO ocr_result (record_id, ocr_text, create_time) VALUES (#{recordId}, #{ocrText}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OcrResult result);
    
    @Select("SELECT * FROM ocr_result WHERE record_id = #{recordId}")
    OcrResult selectByRecordId(Long recordId);
    
    @Delete("DELETE FROM ocr_result WHERE record_id = #{recordId}")
    void deleteByRecordId(Long recordId);
} 