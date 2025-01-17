// src/main/java/com/example/ocr/service/PythonService.java
package com.example.ocr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class PythonService {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    private String getAbsolutePath(String scriptName) {
        try {
            return new File(getClass().getResource("/python/" + scriptName).getFile())
                .getAbsolutePath();
        } catch (Exception e) {
            log.error("获取Python脚本路径失败: {}", scriptName, e);
            throw new RuntimeException("Python脚本不存在: " + scriptName);
        }
    }
    
    public String executeOcrScript(String fileName) {
        try {
            log.info("执行OCR脚本, 文件路径: {}", fileName);
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python",
                "src/main/resources/python/ocr_service.py",
                uploadPath + "/" + fileName
            );
            
            Process process = processBuilder.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // 读取错误
            StringBuilder error = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            log.info("OCR脚本执行完成, 退出码: {}", exitCode);
            
            if (exitCode != 0) {
                throw new RuntimeException("OCR处理失败: " + error);
            }
            
            // 从API响应中提取ParsedText
            String outputStr = output.toString();
            if (outputStr.contains("API Response:")) {
                int apiIndex = outputStr.indexOf("API Response:");
                String jsonStr = outputStr.substring(apiIndex + "API Response:".length()).trim();
                
                // 将单引号替换为双引号以符合JSON格式
                jsonStr = jsonStr.replace("'", "\"")
                        .replace("False", "false")
                        .replace("True", "true");
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonStr);
                JsonNode parsedResults = rootNode.path("ParsedResults");
                
                if (!parsedResults.isEmpty()) {
                    // 合并所有页面的文本
                    StringBuilder fullText = new StringBuilder();
                    for (JsonNode result : parsedResults) {
                        fullText.append(result.path("ParsedText").asText()).append("\n");
                    }
                    return fullText.toString().trim();
                }
            }
            throw new RuntimeException("无法从OCR结果中提取文本内容");
            
        } catch (IOException | InterruptedException e) {
            log.error("执行OCR脚本失败", e);
            throw new RuntimeException("OCR处理失败", e);
        }
    }
    
    public String executeAnalysisScript(String text) {
        try {
            String scriptPath = getAbsolutePath("analysis.py");
            log.info("执行分析脚本: {}", scriptPath);
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python",
                "src/main/resources/python/analysis.py"
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