# 阿里云通义千问大模型配置
ALIYUN_CONFIG = {
    "api": {
        "base_url": "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
        "api_key": "sk-6a5e0c27f6e843f9a4a1eb217178f3a8",
        "model": "qwen-max"
    }
}

# OCR服务配置
OCR_CONFIG = {
    # API配置
    "api": {
        "url": "https://api8.ocr.space/parse/image",
        "key": "donotstealthiskey_ip1",
    },
    
    # OCR选项配置
    "ocr_options": {
        "language": "chs",          # 识别语言：chs(简体中文), cht(繁体中文), eng(英语)等
        "OCREngine": "2",           # 使用引擎2，适合数字和特殊字符
        "scale": "true",            # 自动放大内容（推荐用于低DPI）
        "isTable": "false",         # 表格识别（按需开启）
        "detectOrientation": "false", # 自动检测和旋转方向
        
        # PDF相关选项
        "isOverlayRequired": "false",
        "FileType": ".Auto",
        "IsCreateSearchablePDF": "false",
        "isSearchablePdfHideTextLayer": "true",  # 创建带隐藏文本层的可搜索PDF
        
        # 其他选项
        "detectCheckbox": "false",
        "checkboxTemplate": "0",
    },
    
    # 文件处理配置
    "file_handling": {
        "allowed_extensions": {".pdf", ".png", ".jpg", ".jpeg", ".gif", ".bmp"},
        "max_file_size_mb": 5,      # 最大文件大小（MB）
        "upload_folder": "uploads",  # 上传文件临时存储目录
    },
    
    # 日志配置
    "logging": {
        "enabled": True,
        "save_response": True,      # 是否保存API响应
        "log_folder": "logs",       # 日志存储目录
    },
    
    # 超时设置
    "timeout": 120,  # 请求超时时间（秒）
} 