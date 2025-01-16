package com.example.ocr.controller;

import com.example.ocr.entity.UploadRecord;
import com.example.ocr.service.UploadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/upload")
public class UploadRecordController {
    
    @Autowired
    private UploadRecordService uploadRecordService;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", uploadRecordService.findAll());
        return "upload/list";
    }
    
    @PostMapping
    public String save(UploadRecord record) {
        uploadRecordService.save(record);
        return "redirect:/upload";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        uploadRecordService.delete(id);
        return "redirect:/upload";
    }
    
    @PostMapping("/update")
    public String update(UploadRecord record) {
        uploadRecordService.update(record);
        return "redirect:/upload";
    }
} 