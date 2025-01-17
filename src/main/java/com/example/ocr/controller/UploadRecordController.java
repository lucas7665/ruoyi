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
    private UploadRecordService1 UploadRecordService1;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", UploadRecordService1.findAll());
        return "upload/list";
    }
    
    @PostMapping
    public String save(UploadRecord record) {
        UploadRecordService1.save(record);
        return "redirect:/upload";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        UploadRecordService1.delete(id);
        return "redirect:/upload";
    }
    
    @PostMapping("/update")
    public String update(UploadRecord record) {
        UploadRecordService1.update(record);
        return "redirect:/upload";
    }
} 