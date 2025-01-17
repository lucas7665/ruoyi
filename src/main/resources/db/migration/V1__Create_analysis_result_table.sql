CREATE TABLE IF NOT EXISTS analysis_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL,
    analysis_text TEXT,
    model_answer TEXT COMMENT '模型回答内容',
    create_time DATETIME NOT NULL,
    INDEX idx_record_id (record_id)
); 