import os
import json
from config import OCR_CONFIG, ALIYUN_CONFIG

def get_nursing_config():
    """获取护理系统配置信息"""
    # 使用原有的OCR和阿里云配置
    config = {
        'upload': {
            'path': 'uploads/nursing'  # 相对于项目根目录
        },
        'ocr': OCR_CONFIG,  # 使用原有的OCR配置
        'analysis': {
            'api': ALIYUN_CONFIG['api'],  # 使用原有的阿里云API配置
            'prompt_template': '请分析以下护理记录的规范性和完整性，包括记录是否完整、用语是否规范、是否符合护理记录书写规范等方面：\n{text}',
            'max_tokens': 2000
        },
        'logging': {
            'enabled': True,
            'save_response': True,
            'log_folder': 'logs/nursing'
        }
    }
    
    # 确保上传路径是绝对路径
    if not os.path.isabs(config['upload']['path']):
        config['upload']['path'] = os.path.join(
            os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
            config['upload']['path']
        )
    
    return config 