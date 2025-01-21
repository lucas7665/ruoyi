package com.example.ocr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nursing")
public class NursingConfig {
    private String uploadPath;
    private String pythonPath;
    private String ocrScript;
    private String analysisScript;

    // Getters and Setters
    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public void setPythonPath(String pythonPath) {
        this.pythonPath = pythonPath;
    }

    public String getOcrScript() {
        return ocrScript;
    }

    public void setOcrScript(String ocrScript) {
        this.ocrScript = ocrScript;
    }

    public String getAnalysisScript() {
        return analysisScript;
    }

    public void setAnalysisScript(String analysisScript) {
        this.analysisScript = analysisScript;
    }
} 