package com.example.ocr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.ocr.entity.NursingRecord;
import com.example.ocr.service.NursingService;

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
    @ResponseBody
    public Map<String, Object> analyzeBatch(@RequestBody List<Long> ids) {
        try {
            nursingService.processAnalysis(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return response;
        } catch (Exception e) {
            log.error("分析处理失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
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
    @ResponseBody
    public Map<String, Object> getDetail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> detail = nursingService.getRecordDetail(id);
            response.put("success", true);
            response.put("data", detail);
            return response;
        } catch (Exception e) {
            log.error("获取详情失败", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
} 