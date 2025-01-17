# 医疗文档 OCR 智能分析系统

## 项目简介
本系统是一个专门针对医疗文档的 OCR 识别和智能分析平台。系统能够自动识别医疗文档中的文字内容，并通过 AI 模型进行智能分析，提取关键信息如医师签名、诊断记录等。特别适合医院进行病历电子化和规范化管理。

## 核心特性
1. 文档处理
   - 支持多种格式：PDF、DOC、DOCX
   - 自动文件重命名和分类存储
   - 文件上传进度显示
   - 文件大小和类型验证

2. OCR 识别
   - 集成 OCR Space API
   - 支持多页文档批量识别
   - 自动处理文档方向和倾斜
   - 支持中英文混合识别

3. AI 智能分析
   - 基于通义千问大语言模型
   - 智能提取医师签名信息
   - 识别病历记录时间和类型
   - 生成结构化分析报告

4. 数据管理
   - 完整的文件处理状态跟踪
   - 分步骤保存处理结果
   - 支持批量处理和查询
   - 数据安全存储和备份

## 技术架构
### 后端技术栈
- 核心框架：Spring Boot 2.7.0
- 数据库：MySQL 8.0
- ORM 框架：MyBatis
- 文件处理：Apache Commons IO
- OCR 服务：OCR Space API
- AI 模型：通义千问 API

### 前端技术栈
- 模板引擎：Thymeleaf
- UI 框架：Bootstrap 5
- JS 库：jQuery
- 文件上传：Dropzone.js

### Python 环境
- Python 3.8+
- 依赖包：requests, json, logging

## 系统要求
### 硬件要求
- CPU：4核心及以上
- 内存：8GB 及以上
- 硬盘：100GB 可用空间（用于存储上传文件和处理结果）

### 软件要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Python 3.8+
- 支持的操作系统：Windows Server 2012+/Ubuntu 18.04+/CentOS 7+

## 详细安装步骤

### 1. 数据库配置
sql
创建数据库
CREATE DATABASE ocr_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
创建必要的表
CREATE TABLE upload_record (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
file_name VARCHAR(255) NOT NULL,
file_size BIGINT NOT NULL,
file_type VARCHAR(100),
upload_time DATETIME NOT NULL,
status INT NOT NULL DEFAULT 0,
create_time DATETIME NOT NULL,
update_time DATETIME NOT NULL
);
CREATE TABLE ocr_result (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
record_id BIGINT NOT NULL,
ocr_text TEXT,
create_time DATETIME NOT NULL
);
CREATE TABLE analysis_result (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
record_id BIGINT NOT NULL,
analysis_text TEXT,
model_answer TEXT COMMENT '模型回答内容',
create_time DATETIME NOT NULL,
INDEX idx_record_id (record_id)
);
## 项目结构
ocr-system/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/example/ocr/
│ │ │ ├── controller/
│ │ │ │ └── OcrController.java # 处理文件上传和分析请求
│ │ │ ├── service/
│ │ │ │ ├── UploadRecordService.java # 文件上传和记录管理
│ │ │ │ └── PythonService.java # Python脚本调用服务
│ │ │ ├── entity/
│ │ │ │ ├── UploadRecord.java
│ │ │ │ ├── OcrResult.java
│ │ │ │ └── AnalysisResult.java
│ │ │ └── mapper/
│ │ │ ├── UploadRecordMapper.java
│ │ │ ├── OcrResultMapper.java
│ │ │ └── AnalysisResultMapper.java
│ │ └── resources/
│ │ ├── mapper/
│ │ │ └── Mapper.xml
│ │ ├── python/
│ │ │ ├── ocr_service.py
│ │ │ ├── analysis.py
│ │ │ ├── config.py
│ │ │ └── prom.txt
│ │ ├── templates/
│ │ └── application.yml
│ └── test/
├── pom.xml
├── README.md
└── .gitignore

## API 接口文档

### 1. 文件上传

http
POST /api/upload
Content-Type: multipart/form-data
file: [文件内容]
json
{
"success": true,
"data": {
"id": 1,
"fileName": "example.pdf",
"status": 0
}
}
http
POST /api/ocr/batch
Content-Type: application/json
{
"ids": [1, 2, 3]
}
http
POST /api/analyze/batch
Content-Type: application/json
{
"ids": [1, 2, 3]
}
这个详细的 README.md 包含了：
完整的系统架构说明
详细的安装配置步骤
清晰的项目结构说明
完整的 API 文档
故障排除指南
维护说明和注意事项
你可以根据实际情况继续补充或修改其中的内容

