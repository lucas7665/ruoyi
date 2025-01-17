import requests
import tkinter as tk
from tkinter import filedialog, messagebox
import json
import os

def select_file():
    root = tk.Tk()
    root.withdraw()
    file_path = filedialog.askopenfilename(
        filetypes=[
            ("PDF files", "*.pdf"),
            ("Image files", "*.png;*.jpg;*.jpeg;*.bmp;*.gif")
        ]
    )
    return file_path

def ocr_space_file(filename, overlay=False, api_key='K81445297388957', language='chs'):
    """OCR.space API request with local file"""
    
    api_url = 'https://api.ocr.space/parse/image'
    
    # API请求头
    headers = {
        'apikey': api_key,
    }
    
    # API参数
    payload = {
        'language': language,
        'isOverlayRequired': overlay,
        'detectOrientation': True,
        'scale': True,
        'OCREngine': '2',  # 使用更准确的OCR引擎
    }
    
    try:
        with open(filename, 'rb') as f:
            files = {
                'file': (os.path.basename(filename), f, 'application/pdf')
            }
            
            response = requests.post(api_url,
                                   headers=headers,
                                   files=files,
                                   data=payload,
                                   timeout=30)
            
            response.raise_for_status()  # 检查HTTP错误
            return response.json()
            
    except Exception as e:
        print(f"OCR API Error: {str(e)}")
        return None

def main():
    # 选择文件
    file_path = select_file()
    if not file_path:
        print("没有选择文件！")
        return
    
    print("开始处理，请稍候...")
    
    # 执行OCR
    result = ocr_space_file(file_path)
    
    if isinstance(result, dict):
        # 保存完整JSON结果
        with open('ocr_result_full.json', 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=2)
        
        if 'ParsedResults' in result and len(result['ParsedResults']) > 0:
            # 处理所有页面的文本
            all_text = []
            for i, page_result in enumerate(result['ParsedResults']):
                page_text = page_result.get('ParsedText', '')
                if page_text.strip():
                    all_text.append(f"\n=== 第 {i + 1} 页 ===\n")
                    all_text.append(page_text)
            
            # 合并所有页面的文本
            combined_text = '\n'.join(all_text)
            print("\n识别的文本内容:")
            print(combined_text)
            
            # 保存文本结果到文件
            with open('ocr_result_text.txt', 'w', encoding='utf-8') as f:
                f.write(combined_text)
            print("\n结果已保存到 ocr_result_text.txt 和 ocr_result_full.json")
            
            # 显示成功消息
            messagebox.showinfo("完成", "文字识别完成！结果已保存到文件。")
        else:
            print("未能识别到文本内容")
            print("完整响应:", json.dumps(result, ensure_ascii=False, indent=2))
            messagebox.showerror("错误", "未能识别到文本内容")
    else:
        print(f"发生错误: {result}")
        messagebox.showerror("错误", str(result))

if __name__ == "__main__":
    main()