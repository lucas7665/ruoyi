// src/main/java/com/example/ocr/controller/OcrController.java
package com.example.ocr.controller;

import com.example.ocr.entity.UploadRecord;
import com.example.ocr.service.UploadRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/ocr")
public class OcrController {
    
    @Autowired
    private UploadRecordService uploadRecordService;
    
    @GetMapping({"", "/", "/index"})
    public String index() {
        return "index";  // 对应 templates/index.html
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("开始处理文件上传: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // 验证文件
        if (file.isEmpty()) {
            log.error("上传的文件为空");
            return ResponseEntity.badRequest().body("文件不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        log.info("文件类型: {}", contentType);
        if (contentType == null || (!contentType.equals("application/pdf") 
                && !contentType.startsWith("image/"))) {
            log.error("不支持的文件类型: {}", contentType);
            return ResponseEntity.badRequest().body("只支持PDF和图片文件");
        }
        
        try {
            UploadRecord record = uploadRecordService.processFile(file);
            log.info("文件处理成功: {}", record);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", record.getId());
            response.put("fileName", record.getFileName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("文件处理失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/analyze/{id}")
    public ResponseEntity<?> analyzeText(@PathVariable Long id) {
        log.info("开始分析文本, ID: {}", id);
        try {
            uploadRecordService.processAnalysis(Arrays.asList(id));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("分析失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentRecords() {
        return ResponseEntity.ok(uploadRecordService.getRecentRecords(5));
    }
    
    @PostMapping("/analyze/batch")
    public ResponseEntity<?> analyzeTextBatch(@RequestBody Map<String, List<Long>> request) {
        log.info("开始批量分析, IDs: {}", request.get("ids"));
        try {
            List<Long> ids = request.get("ids");
            // 根据状态选择调用不同的处理方法
            uploadRecordService.processOcr(ids);      // 处理待识别的文件
            uploadRecordService.processAnalysis(ids); // 处理待分析的文件
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("批量分析失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        log.info("开始删除记录, ID: {}", id);
        try {
            uploadRecordService.deleteRecord(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/recognize/batch")
    public ResponseEntity<?> recognizeTextBatch(@RequestBody Map<String, List<Long>> request) {
        log.info("开始批量识别, IDs: {}", request.get("ids"));
        try {
            List<Long> ids = request.get("ids");
            uploadRecordService.processOcr(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("批量识别失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}