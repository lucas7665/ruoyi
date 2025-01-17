// src/main/java/com/example/ocr/service/UploadRecordService.java
package com.example.ocr.service;

import com.example.ocr.entity.UploadRecord;
import com.example.ocr.entity.OcrResult;
import com.example.ocr.entity.AnalysisResult;
import com.example.ocr.mapper.UploadRecordMapper;
import com.example.ocr.mapper.OcrResultMapper;
import com.example.ocr.mapper.AnalysisResultMapper;
import com.example.ocr.config.FileStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class UploadRecordService {
    
    @Autowired
    private UploadRecordMapper uploadRecordMapper;
    
    @Autowired
    private OcrResultMapper ocrResultMapper;
    
    @Autowired
    private AnalysisResultMapper analysisResultMapper;
    
    @Autowired
    private PythonService pythonService;
    
    @Autowired
    private FileStorageConfig fileStorageConfig;
    
    public UploadRecord processFile(MultipartFile file) throws IOException {
        // 确保上传目录存在
        File uploadDir = new File(fileStorageConfig.getPath());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成文件名
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        // 使用相对路径
        Path filePath = Paths.get(fileStorageConfig.getPath(), fileName).toAbsolutePath().normalize();
        
        log.info("文件将保存到: {}", filePath);
        
        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 创建并保存记录
        UploadRecord record = new UploadRecord();
        record.setFileName(file.getOriginalFilename());
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType());
        record.setUploadTime(LocalDateTime.now());
        record.setStatus(0); // 待识别
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        
        uploadRecordMapper.insert(record);
        return record;
    }
    
    // OCR识别处理
    public void processOcr(List<Long> ids) {
        for (Long id : ids) {
            try {
                UploadRecord record = uploadRecordMapper.selectById(id);
                if (record == null || record.getStatus() != 0) {
                    continue;
                }
                
                // 获取文件的完整路径
                File[] files = new File(fileStorageConfig.getPath()).listFiles((dir, name) -> 
                    name.endsWith("_" + record.getFileName()));
                
                if (files == null || files.length == 0) {
                    log.error("找不到文件: {}", record.getFileName());
                    continue;
                }
                
                // 使用实际的文件名（包含UUID前缀）
                String actualFileName = files[0].getName();
                log.info("找到文件: {}", actualFileName);
                
                // 执行OCR
                String ocrResult = pythonService.executeOcrScript(actualFileName);
                log.info("OCR结果预览: {}", ocrResult.substring(0, Math.min(200, ocrResult.length())));
                
                if (ocrResult != null && !ocrResult.startsWith("Error:")) {
                    // 保存OCR结果
                    OcrResult ocr = new OcrResult();
                    ocr.setRecordId(id);
                    ocr.setOcrText(ocrResult);
                    ocr.setCreateTime(LocalDateTime.now());
                    ocrResultMapper.insert(ocr);
                    
                    // 更新状态为待分析(1)
                    record.setStatus(1);
                    record.setUpdateTime(LocalDateTime.now());
                    uploadRecordMapper.updateById(record);
                    
                    log.info("OCR处理完成, ID: {}", id);
                } else {
                    log.error("OCR处理失败: {}", ocrResult);
                }
            } catch (Exception e) {
                log.error("OCR处理失败, ID: {}", id, e);
            }
        }
    }
    
    // 分析处理
    public void processAnalysis(List<Long> ids) {
        for (Long id : ids) {
            try {
                UploadRecord record = uploadRecordMapper.selectById(id);
                if (record == null || record.getStatus() != 1) {
                    continue;
                }
                
                // 获取OCR结果
                OcrResult ocr = ocrResultMapper.selectByRecordId(id);
                if (ocr == null || ocr.getOcrText() == null) {
                    log.error("找不到OCR结果, ID: {}", id);
                    continue;
                }
                
                // 解析OCR结果
                try {
                    String ocrText = ocr.getOcrText();
                    // OCR文本已经是提取好的文本内容，直接使用
                    if (ocrText.trim().isEmpty()) {
                        log.error("OCR文本内容为空, ID: {}", id);
                        continue;
                    }
                    
                    // 执行分析
                    String analysisResult = pythonService.executeAnalysisScript(ocrText);
                    
                    // 从分析结果中提取模型回答
                    String modelAnswer = "";
                    if (analysisResult.contains("模型回答:")) {
                        int startIndex = analysisResult.indexOf("模型回答:") + "模型回答:".length();
                        int endIndex = analysisResult.indexOf("2025-", startIndex);
                        if (endIndex == -1) {
                            // 如果找不到下一个时间戳，就取到末尾
                            modelAnswer = analysisResult.substring(startIndex).trim();
                        } else {
                            modelAnswer = analysisResult.substring(startIndex, endIndex).trim();
                        }
                    } else {
                        log.error("无法从分析结果中提取模型回答");
                        throw new RuntimeException("无法从分析结果中提取模型回答");
                    }
                    
                    // 保存分析结果
                    AnalysisResult analysis = new AnalysisResult();
                    analysis.setRecordId(id);
                    analysis.setAnalysisText(analysisResult);
                    analysis.setModelAnswer(modelAnswer);
                    analysis.setCreateTime(LocalDateTime.now());
                    analysisResultMapper.insert(analysis);
                    
                    // 更新状态为已分析(2)
                    record.setStatus(2);
                    record.setUpdateTime(LocalDateTime.now());
                    uploadRecordMapper.updateById(record);
                    
                    log.info("分析处理完成, ID: {}", id);
                } catch (Exception e) {
                    log.error("解析OCR结果失败: {}", e.getMessage());
                    throw e;
                }
            } catch (Exception e) {
                log.error("分析处理失败, ID: {}", id, e);
            }
        }
    }
    
    // 删除记录
    public void deleteRecord(Long id) {
        // 删除相关数据
        ocrResultMapper.deleteByRecordId(id);
        analysisResultMapper.deleteByRecordId(id);
        
        // 更新记录状态为已删除
        UploadRecord record = uploadRecordMapper.selectById(id);
        if (record != null) {
            record.setStatus(-1);
            record.setUpdateTime(LocalDateTime.now());
            uploadRecordMapper.updateById(record);
        }
    }
    
    public List<UploadRecord> getRecentRecords(int limit) {
        return uploadRecordMapper.selectRecentRecords(limit);
    }
    
    public Map<String, Object> getRecordDetail(Long id) {
        Map<String, Object> detail = new HashMap<>();
        
        // 获取上传记录
        UploadRecord record = uploadRecordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        detail.put("record", record);
        
        // 获取OCR结果
        OcrResult ocrResult = ocrResultMapper.selectByRecordId(id);
        if (ocrResult != null) {
            detail.put("ocrResult", ocrResult);
        }
        
        // 获取分析结果
        AnalysisResult analysisResult = analysisResultMapper.selectByRecordId(id);
        if (analysisResult != null) {
            detail.put("analysisResult", analysisResult);
        }
        
        return detail;
    }
}