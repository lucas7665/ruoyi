package com.example.ocr.service;

import com.example.ocr.entity.UploadRecord;
import com.example.ocr.mapper.UploadRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UploadRecordService {
    
    @Autowired
    private UploadRecordMapper uploadRecordMapper;
    
    public List<UploadRecord> findAll() {
        return uploadRecordMapper.findByStatus(1);
    }
    
    public UploadRecord save(UploadRecord record) {
        record.setUploadTime(LocalDateTime.now());
        uploadRecordMapper.insert(record);
        return record;
    }
    
    public void delete(Long id) {
        uploadRecordMapper.updateStatus(id, 0);
    }
    
    public UploadRecord update(UploadRecord record) {
        // 根据需要实现更新逻辑
        return record;
    }
} 