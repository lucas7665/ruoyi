// src/main/java/com/example/ocr/mapper/UploadRecordMapper.java
package com.example.ocr.mapper;

import com.example.ocr.entity.UploadRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UploadRecordMapper {
    
    @Insert("INSERT INTO upload_record (file_name, file_size, file_type, upload_time, status, create_time, update_time) " +
            "VALUES (#{fileName}, #{fileSize}, #{fileType}, #{uploadTime}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UploadRecord record);
    
    @Select("SELECT * FROM upload_record WHERE id = #{id}")
    UploadRecord selectById(Long id);
    
    @Update("UPDATE upload_record SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(UploadRecord record);
    
    @Select("SELECT * FROM upload_record WHERE status != -1 ORDER BY upload_time DESC LIMIT #{limit}")
    List<UploadRecord> selectRecentRecords(int limit);
}