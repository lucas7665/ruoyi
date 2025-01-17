import sys
import os
from ocr_space import ocr_space_file

def ocr_file(file_path):
    try:
        # 确保文件存在
        if not os.path.exists(file_path):
            raise Exception(f"File not found: {file_path}")
            
        # 获取完整的文件路径
        full_path = os.path.abspath(file_path)
        print(f"Processing file: {full_path}")
        
        # 调用OCR API
        result = ocr_space_file(
            filename=full_path,
            language='chs',  # 中文识别
            overlay=False
        )
        
        if result is None:
            raise Exception("OCR API call failed")
            
        # 打印完整响应用于调试
        print(f"API Response: {result}")
        
        # 提取识别文本
        if result and 'ParsedResults' in result and result['ParsedResults']:
            text = result['ParsedResults'][0].get('ParsedText', '')
            if text.strip():
                print(f"Extracted text: {text[:100]}...")  # 打印前100个字符
                return text
            else:
                raise Exception("No text extracted from OCR results")
        else:
            error_message = result.get('ErrorMessage', 'Unknown error')
            raise Exception(f"OCR failed: {error_message}")
            
    except Exception as e:
        error_msg = f"Error: {str(e)}"
        print(error_msg, file=sys.stderr)
        return error_msg

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python ocr_service.py <file_path>")
        sys.exit(1)
        
    file_path = sys.argv[1]
    result = ocr_file(file_path)
    if result and not result.startswith('Error:'):
        print(result)  # 输出识别结果
        sys.exit(0)
    else:
        sys.exit(1) 