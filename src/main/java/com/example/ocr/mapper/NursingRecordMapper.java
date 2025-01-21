package com.example.ocr.mapper;

import com.example.ocr.entity.NursingRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NursingRecordMapper {
    
    @Select("SELECT * FROM nursing_record WHERE id = #{id}")
    NursingRecord selectById(Long id);
    
    @Select("SELECT * FROM nursing_record WHERE status != -1 ORDER BY upload_time DESC LIMIT #{limit}")
    List<NursingRecord> selectRecentRecords(int limit);
    
    @Insert("INSERT INTO nursing_record (file_name, stored_file_name, file_size, file_type, " +
            "start_page, end_page, total_pages, upload_time, status, create_time, update_time) " +
            "VALUES (#{fileName}, #{storedFileName}, #{fileSize}, #{fileType}, " +
            "#{startPage}, #{endPage}, #{totalPages}, #{uploadTime}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NursingRecord record);
    
    @Update("UPDATE nursing_record SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(NursingRecord record);
    
    @Select("SELECT COUNT(*) FROM nursing_record WHERE file_name = #{fileName} AND status != -1")
    int countByFileName(String fileName);
} 