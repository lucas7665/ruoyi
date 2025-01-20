package com.example.ocr.controller;

import com.example.ocr.entity.UploadRecord;
import com.example.ocr.service.UploadRecordService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/upload")
public class UploadRecordController {
    
    @Autowired
    private UploadRecordService1 uploadRecordService1;
    
    @GetMapping("/list")
    public String list(Model model) {
        return "medical_control.html";
    }
    
    @PostMapping
    public String save(UploadRecord record) {
        uploadRecordService1.save(record);
        return "redirect:/upload";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        uploadRecordService1.delete(id);
        return "redirect:/upload";
    }
    
    @PostMapping("/update")
    public String update(UploadRecord record) {
        uploadRecordService1.update(record);
        return "redirect:/upload";
    }
} 