package com.example.ocr.service;

import com.example.ocr.entity.*;
import com.example.ocr.mapper.*;
import com.example.ocr.config.NursingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.File;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Slf4j
@Service
public class NursingService {
    
    @Autowired
    private NursingRecordMapper nursingRecordMapper;
    
    @Autowired
    private NursingOcrResultMapper ocrResultMapper;
    
    @Autowired
    private NursingAnalysisResultMapper analysisResultMapper;
    
    @Autowired
    private NursingConfig nursingConfig;
    
    @Autowired
    private PythonService pythonService;
    
    public List<NursingRecord> getRecentRecords(int limit) {
        log.info("获取最近{}条记录", limit);
        List<NursingRecord> records = nursingRecordMapper.selectRecentRecordsWithAnalysis(limit);
        log.info("查询到{}条记录", records.size());
        return records;
    }
    
    public NursingRecord processFile(MultipartFile file, Integer startPage, Integer endPage, Integer totalPages) throws IOException {
        log.info("开始处理文件上传: fileName={}, startPage={}, endPage={}, totalPages={}", 
                file.getOriginalFilename(), startPage, endPage, totalPages);
        
        // 检查文件是否已存在
        if (nursingRecordMapper.countByFileName(file.getOriginalFilename()) > 0) {
            log.warn("文件已存在: {}", file.getOriginalFilename());
            throw new RuntimeException("文件已经上传过，请换个文件重新尝试");
        }
        
        // 生成唯一文件名
        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        // 确保上传目录存在
        File uploadDir = new File(nursingConfig.getUploadPath());
        if (!uploadDir.exists()) {
            log.info("创建上传目录: {}", uploadDir.getAbsolutePath());
            uploadDir.mkdirs();
        }
        
        // 保存文件
        Path filePath = Paths.get(nursingConfig.getUploadPath(), storedFileName).toAbsolutePath().normalize();
        log.info("保存文件: {}", filePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 创建记录
        NursingRecord record = new NursingRecord();
        record.setFileName(file.getOriginalFilename());
        record.setStoredFileName(storedFileName);
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType());
        record.setStartPage(startPage);
        record.setEndPage(endPage);
        record.setTotalPages(totalPages);
        record.setUploadTime(LocalDateTime.now());
        record.setStatus(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        
        log.info("插入数据库记录: {}", record);
        nursingRecordMapper.insert(record);
        log.info("文件处理完成: id={}", record.getId());
        return record;
    }

    public void processOcr(List<Long> ids) {
        log.info("开始批量OCR处理: ids={}", ids);
        for (Long id : ids) {
            try {
                log.info("处理记录: id={}", id);
                NursingRecord record = nursingRecordMapper.selectById(id);
                if (record != null && record.getStatus() == 0) {
                    // 执行OCR脚本
                    String scriptPath = Paths.get(nursingConfig.getPythonPath(), "nursing_ocr.py").toString();
                    String filePath = Paths.get(nursingConfig.getUploadPath(), record.getStoredFileName()).toString();
                    
                    log.info("执行OCR脚本: script={}, file={}, startPage={}, endPage={}", 
                        scriptPath, filePath, record.getStartPage(), record.getEndPage());
                    
                    String ocrResult = pythonService.executeOcrScript(filePath, record.getStartPage(), record.getEndPage());
                    
                    // 保存OCR结果
                    NursingOcrResult ocrRecord = new NursingOcrResult();
                    ocrRecord.setRecordId(record.getId());
                    ocrRecord.setOcrText(ocrResult);
                    ocrRecord.setCreateTime(LocalDateTime.now());
                    ocrResultMapper.insert(ocrRecord);
                    
                    // 更新记录状态
                    record.setStatus(1);
                    record.setUpdateTime(LocalDateTime.now());
                    nursingRecordMapper.updateById(record);
                    log.info("OCR处理完成: id={}", id);
                }
            } catch (Exception e) {
                log.error("OCR处理失败: id={}, error={}", id, e.getMessage(), e);
                throw new RuntimeException("OCR处理失败: " + e.getMessage());
            }
        }
    }

    public void processAnalysis(List<Long> ids) {
        log.info("开始批量分析处理: ids={}", ids);
        for (Long id : ids) {
            try {
                NursingRecord record = nursingRecordMapper.selectById(id);
                if (record != null && record.getStatus() == 1) {
                    // 获取OCR结果
                    NursingOcrResult ocrResult = ocrResultMapper.selectByRecordId(record.getId());
                    if (ocrResult == null || ocrResult.getOcrText() == null) {
                        throw new RuntimeException("找不到OCR结果");
                    }
                    
                    // 执行分析脚本
                    String analysisResult = pythonService.executeAnalysisScript(ocrResult.getOcrText());
                    
                    // 提取模型回答部分
                    String modelAnswer = extractModelAnswer(analysisResult);
                    
                    // 检查异常项
                    int abnormalCount = checkAbnormalItems(modelAnswer);
                    
                    // 保存分析结果
                    NursingAnalysisResult analysisRecord = new NursingAnalysisResult();
                    analysisRecord.setRecordId(record.getId());
                    analysisRecord.setAnalysisText(ocrResult.getOcrText());
                    analysisRecord.setModelAnswer(modelAnswer);
                    analysisRecord.setAbnormalCount(abnormalCount);
                    analysisRecord.setCreateTime(LocalDateTime.now());
                    analysisResultMapper.insert(analysisRecord);
                    
                    // 更新记录状态
                    record.setStatus(2);
                    record.setUpdateTime(LocalDateTime.now());
                    nursingRecordMapper.updateById(record);
                    log.info("分析处理完成: id={}", id);
                }
            } catch (Exception e) {
                log.error("分析处理失败: id={}, error={}", id, e.getMessage(), e);
                throw new RuntimeException("分析处理失败: " + e.getMessage());
            }
        }
    }

    private String extractModelAnswer(String output) {
        // 查找模型回答的开始标记
        String startMarker = "模型回答: \n";
        int start = output.indexOf(startMarker);
        if (start == -1) {
            log.warn("未找到模型回答标记，返回完整输出");
            return output;
        }
        
        // 提取模型回答部分（从标记后开始，到下一个时间戳或文件结尾）
        start += startMarker.length();
        int end = output.indexOf("\n202", start); // 查找下一个时间戳
        if (end == -1) {
            end = output.length();
        }
        
        return output.substring(start, end).trim();
    }

    private int checkAbnormalItems(String modelAnswer) {
        int abnormalCount = 0;
        // 检查是否包含未签名相关的内容
        Pattern pattern = Pattern.compile("未签名|缺少签名|没有签名|签名缺失");
        Matcher matcher = pattern.matcher(modelAnswer);
        while (matcher.find()) {
            abnormalCount++;
        }
        return abnormalCount;
    }

    public Map<String, Object> getRecordDetail(Long id) {
        Map<String, Object> detail = new HashMap<>();
        
        // 获取基本信息
        NursingRecord record = nursingRecordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        detail.put("record", record);
        
        // 获取OCR结果
        NursingOcrResult ocrResult = ocrResultMapper.selectByRecordId(id);
        if (ocrResult != null) {
            detail.put("ocrResult", ocrResult);
        }
        
        // 获取分析结果
        NursingAnalysisResult analysisResult = analysisResultMapper.selectByRecordId(id);
        if (analysisResult != null) {
            detail.put("analysisResult", analysisResult);
        }
        
        return detail;
    }

    public void deleteRecords(List<Long> ids) {
        log.info("开始删除记录: ids={}", ids);
        for (Long id : ids) {
            try {
                NursingRecord record = nursingRecordMapper.selectById(id);
                if (record != null) {
                    // 设置状态为已删除
                    record.setStatus(-1);
                    record.setUpdateTime(LocalDateTime.now());
                    nursingRecordMapper.updateById(record);
                    
                    // 删除物理文件
                    Path filePath = Paths.get(nursingConfig.getUploadPath(), record.getStoredFileName());
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                    }
                    
                    log.info("记录删除成功: id={}", id);
                }
            } catch (Exception e) {
                log.error("删除记录失败: id={}, error={}", id, e.getMessage(), e);
                throw new RuntimeException("删除记录失败: " + e.getMessage());
            }
        }
    }
} 