package com.example.ocr.mapper;

import com.example.ocr.entity.UploadRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UploadRecordMapper {
    @Select("SELECT * FROM upload_record WHERE status = #{status}")
    List<UploadRecord> findByStatus(Integer status);
    
    @Insert("INSERT INTO upload_record(file_name, file_size, file_type, upload_time, upload_user, status) " +
            "VALUES(#{fileName}, #{fileSize}, #{fileType}, #{uploadTime}, #{uploadUser}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UploadRecord record);
    
    @Update("UPDATE upload_record SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Select("SELECT * FROM upload_record WHERE id = #{id}")
    UploadRecord findById(Long id);
} 