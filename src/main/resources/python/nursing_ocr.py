import sys
import os
import json
import requests
import time
from config import OCR_CONFIG

def process_pdf(pdf_path, start_page=1, end_page=None):
    """处理PDF文件并返回OCR结果"""
    try:
        # 检查文件是否存在
        if not os.path.exists(pdf_path):
            raise Exception(f"File not found: {pdf_path}")
            
        # 获取完整的文件路径
        full_path = os.path.abspath(pdf_path)
        
        # 准备OCR请求参数
        payload = OCR_CONFIG['ocr_options'].copy()
        payload['apikey'] = OCR_CONFIG['api']['key']
        
        # 发送OCR请求
        with open(full_path, 'rb') as f:
            files = {'file': f}
            response = requests.post(
                OCR_CONFIG['api']['url'],
                files=files,
                data=payload,
                timeout=OCR_CONFIG['timeout']
            )
            
        if response.status_code != 200:
            raise Exception(f"OCR API request failed with status code: {response.status_code}")
            
        result = response.json()
        
        if result and 'ParsedResults' in result:
            texts = []
            for parsed_result in result['ParsedResults']:
                text = parsed_result.get('ParsedText', '').strip()
                if text:
                    texts.append(text)
            
            if texts:
                return "\n".join(texts)
            else:
                raise Exception("No text was extracted from the file")
        else:
            error_message = result.get('ErrorMessage', 'Unknown error')
            raise Exception(f"OCR failed: {error_message}")
            
    except Exception as e:
        print(f"Error: {str(e)}", file=sys.stderr)
        raise

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python nursing_ocr.py <pdf_path> [start_page] [end_page]", file=sys.stderr)
        sys.exit(1)
        
    try:
        result = process_pdf(sys.argv[1])
        print(result)
        sys.exit(0)
    except Exception as e:
        sys.exit(1) 