import sys
import json
import logging
import requests
from config import ALIYUN_CONFIG

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

def load_prompt_template():
    """加载提示词模板"""
    try:
        with open('src/main/resources/python/prom.txt', 'r', encoding='utf-8') as f:
            return f.read()
    except Exception as e:
        logger.error(f"加载提示词模板失败: {e}")
        return None

def call_qwen_api(prompt, content):
    """调用通义千问API"""
    headers = {
        'Authorization': f'Bearer {ALIYUN_CONFIG["api"]["api_key"]}',
        'Content-Type': 'application/json'
    }
    
    # 构建完整的提示词
    full_prompt = f"{prompt}\n\n文档内容：\n{content}"
    
    payload = {
        "model": ALIYUN_CONFIG["api"]["model"],
        "input": {
            "messages": [
                {
                    "role": "system",
                    "content": "你是一个专业的医疗文档分析助手，请对医疗文档进行分析并提供专业的见解。"
                },
                {
                    "role": "user",
                    "content": full_prompt
                }
            ]
        }
    }
    
    logger.info(f"正在调用通义千问API, 模型: {ALIYUN_CONFIG['api']['model']}")
    # 只记录提示词的前200个字符，避免日志过长
    logger.info(f"提示词预览: {full_prompt[:200]}...")
    
    response = requests.post(
        ALIYUN_CONFIG["api"]["base_url"],
        headers=headers,
        json=payload
    )
    
    if response.status_code != 200:
        raise Exception(f"API调用失败: {response.text}")
        
    return response.json()

def analyze_medical_text(text):
    try:
        logger.info("开始医疗文本分析")
        # 只记录文本预览，但传递完整文本
        logger.info(f"输入文本预览(前100个字符): {text[:100]}...")
        
        # 加载提示词模板
        prompt_template = load_prompt_template()
        if not prompt_template:
            raise Exception("无法加载提示词模板")
            
        # 调用API获取回答，传递完整文本
        logger.info("开始调用通义千问API...")
        response = call_qwen_api(prompt_template, text)
        logger.info(f"API响应: \n{json.dumps(response, ensure_ascii=False, indent=2)}")
        
        # 提取模型回答
        model_response = response['output']['text']
        logger.info(f"模型回答: \n{model_response}")
        
        # 解析回答，构建结构化输出
        analysis = {
            "summary": model_response,
            "findings": [],
            "recommendations": []
        }
        
        logger.info(f"分析结果: {json.dumps(analysis, ensure_ascii=False, indent=2)}")
        return json.dumps(analysis, ensure_ascii=False)
        
    except Exception as e:
        error_msg = f"分析过程出错: {str(e)}"
        logger.error(error_msg, exc_info=True)
        return json.dumps({"error": error_msg}, ensure_ascii=False)

if __name__ == "__main__":
    logger.info("开始执行分析脚本")
    
    # 从标准输入读取文本
    text = sys.stdin.read()
    if not text.strip():
        error_msg = "Error: No input text provided"
        logger.error(error_msg)
        print(error_msg, file=sys.stderr)
        sys.exit(1)
    
    logger.info("成功读取输入文本")
    result = analyze_medical_text(text)
    print(result)
    logger.info("分析完成") 