package com.example.ocr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.ocr.entity.NursingRecord;
import com.example.ocr.service.NursingService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/nursing")
public class NursingController {
    
    @Autowired
    private NursingService nursingService;
    
    @GetMapping("/manager")
    public String manager() {
        return "huli_manager";
    }
    
    @GetMapping("/recent")
    @ResponseBody
    public List<NursingRecord> getRecentRecords() {
        return nursingService.getRecentRecords(20);
    }
    
    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("startPage") Integer startPage,
                                    @RequestParam("endPage") Integer endPage,
                                    @RequestParam("totalPages") Integer totalPages) {
        Map<String, Object> response = new HashMap<>();
        try {
            NursingRecord record = nursingService.processFile(file, startPage, endPage, totalPages);
            response.put("success", true);
            response.put("id", record.getId());
            return response;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/ocr/batch")
    @ResponseBody
    public Map<String, Object> recognizeTextBatch(@RequestBody List<Long> ids) {
        try {
            nursingService.processOcr(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return response;
        } catch (Exception e) {
            log.error("OCR处理失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/analyze/batch")
    public ResponseEntity<?> analyzeBatch(@RequestBody List<Long> ids) {
        try {
            nursingService.processAnalysis(ids);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            log.error("分析处理失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            nursingService.deleteRecords(ids);
            response.put("success", true);
            return response;
        } catch (Exception e) {
            log.error("删除失败", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        try {
            Map<String, Object> detail = nursingService.getRecordDetail(id);
            if (detail == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("获取记录详情失败: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "获取记录详情失败: " + e.getMessage()));
        }
    }
} 