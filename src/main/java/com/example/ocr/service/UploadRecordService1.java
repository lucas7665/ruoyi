package com.example.ocr.service;

import com.example.ocr.entity.UploadRecord;
import com.example.ocr.mapper.UploadRecordMapper1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UploadRecordService1 {
    
    @Autowired
    private UploadRecordMapper1 UploadRecordMapper1;
    
    public List<UploadRecord> findAll() {
        return UploadRecordMapper1.findByStatus(1);
    }
    
    public UploadRecord save(UploadRecord record) {
        record.setUploadTime(LocalDateTime.now());
        UploadRecordMapper1.insert(record);
        return record;
    }
    
    public void delete(Long id) {
        UploadRecordMapper1.updateStatus(id, 0);
    }
    
    public UploadRecord update(UploadRecord record) {
        // 根据需要实现更新逻辑
        return record;
    }
} 