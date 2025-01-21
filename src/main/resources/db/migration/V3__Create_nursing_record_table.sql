-- 护理记录表
CREATE TABLE nursing_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    stored_file_name VARCHAR(255) NOT NULL COMMENT '存储的文件名',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(100) COMMENT '文件类型',
    start_page INT COMMENT '开始页码',
    end_page INT COMMENT '结束页码',
    total_pages INT COMMENT '总页数',
    upload_time DATETIME NOT NULL COMMENT '上传时间',
    status INT NOT NULL DEFAULT 0 COMMENT '状态：0-待识别，1-待分析，2-已分析，-1-已删除',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_upload_time (upload_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理记录表';

-- OCR结果表
CREATE TABLE nursing_ocr_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '关联的护理记录ID',
    ocr_text MEDIUMTEXT COMMENT 'OCR识别结果文本',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    FOREIGN KEY (record_id) REFERENCES nursing_record(id),
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理OCR结果表';

-- 分析结果表
CREATE TABLE nursing_analysis_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '关联的护理记录ID',
    analysis_text MEDIUMTEXT COMMENT '分析文本',
    model_answer MEDIUMTEXT COMMENT 'AI模型回答',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    FOREIGN KEY (record_id) REFERENCES nursing_record(id),
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理分析结果表'; 