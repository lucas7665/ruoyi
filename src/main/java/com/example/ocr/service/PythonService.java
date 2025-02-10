// src/main/java/com/example/ocr/service/PythonService.java
package com.example.ocr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

@Slf4j
@Service
public class PythonService {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${python.script.path}")
    private String scriptPath;
    
    private String getProjectPath() {
        return System.getProperty("user.dir");
    }
    
    private String getPythonPath() {
        return Paths.get(getProjectPath(), "venv", "bin", "python3").toString();
    }
    
    public String executeOcrScript(String fileName) throws IOException {
        return executeOcrScript(fileName, null, null);
    }
    
    public String executeOcrScript(String fileName, Integer startPage, Integer endPage) throws IOException {
        log.info("准备执行OCR脚本，文件路径: {}", fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            log.error("文件不存在: {}", fileName);
            throw new RuntimeException("文件不存在: " + fileName);
        }
        log.info("文件存在，大小: {} bytes", file.length());

        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("src/main/resources/python/nursing_ocr.py");
        command.add(fileName);
        command.add(startPage == null ? "null" : startPage.toString());
        command.add(endPage == null ? "null" : endPage.toString());
        
        log.info("执行OCR命令: command={}, workDir={}", 
            String.join(" ", command), System.getProperty("user.dir"));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        log.info("Python进程已启动");
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("Python输出: {}", line);
                output.append(line).append("\n");
            }
            log.info("Python脚本执行完成");
            return output.toString();
        } catch (Exception e) {
            log.error("执行Python脚本时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("OCR处理失败: " + e.getMessage());
        }
    }
    
    public String executeAnalysisScript(String text) {
        try {
            String projectPath = getProjectPath();
            String fullScriptPath = Paths.get(projectPath, scriptPath, "analysis.py").toString();
            log.info("执行分析脚本: {}", fullScriptPath);
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                getPythonPath(),
                fullScriptPath
            );
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 将文本写入Python进程
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(text);
            }
            
            // 读取分析结果
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            log.info("分析脚本执行完成, 退出码: {}", exitCode);
            
            if (exitCode != 0) {
                throw new RuntimeException("分析处理失败: " + output);
            }
            
            String result = output.toString().trim();
            log.info("分析脚本输出: {}", result);
            return result;
            
        } catch (IOException | InterruptedException e) {
            log.error("执行分析脚本失败", e);
            throw new RuntimeException("分析处理失败", e);
        }
    }
}