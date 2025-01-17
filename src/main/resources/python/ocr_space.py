# type: ignore
import requests
import json
import os

def ocr_space_file(filename, overlay=False, api_key='K81445297388957', language='chs'):
    """OCR.space API request with local file"""
    
    api_url = 'https://api.ocr.space/parse/image'
    
    headers = {
        'apikey': api_key,
    }
    
    payload = {
        'language': language,
        'isOverlayRequired': overlay,
        'detectOrientation': True,
        'scale': True,
        'OCREngine': '2',
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
            
            response.raise_for_status()
            return response.json()
            
    except Exception as e:
        print(f"OCR API Error: {str(e)}")
        return None