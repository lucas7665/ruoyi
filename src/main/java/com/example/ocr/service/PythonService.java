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
    
    public String executeOcrScript(String filePath, Integer startPage, Integer endPage) throws IOException {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(Paths.get(scriptPath, "nursing_ocr.py").toString());
        command.add(filePath);
        command.add(String.valueOf(startPage));
        command.add(String.valueOf(endPage));
        
        return executePythonScript(command);
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
    
    private String executePythonScript(List<String> command) throws IOException {
        log.info("执行Python命令: {}", String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug("Python输出: {}", line);
            }
        }
        
        return output.toString().trim();
    }
}