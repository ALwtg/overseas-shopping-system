#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
下载产品图片到本地
"""
import os
import requests
import pymysql
from urllib.parse import urlparse

def download_image(url, save_path):
    """下载单张图片"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, timeout=30)
        if response.status_code == 200:
            with open(save_path, 'wb') as f:
                f.write(response.content)
            return True
    except Exception as e:
        print(f"下载失败 {url}: {e}")
    return False

def main():
    # 本地图片存储路径
    base_path = "D:/daigou-uploads/images/"
    os.makedirs(base_path, exist_ok=True)
    
    # 数据库连接配置（请根据实际情况修改）
    db_config = {
        'host': 'localhost',
        'user': 'root',
        'password': '',  # XAMPP 默认无密码
        'database': 'daigou',
        'charset': 'utf8mb4'
    }
    
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 获取所有产品的图片URL
        cursor.execute("SELECT id, name, cover_image FROM product WHERE cover_image LIKE 'http%'")
        products = cursor.fetchall()
        
        print(f"找到 {len(products)} 个需要下载图片的产品")
        
        updated_count = 0
        for product_id, name, cover_image in products:
            try:
                # 从URL生成文件名
                parsed = urlparse(cover_image)
                filename = os.path.basename(parsed.path.split('?')[0])
                if not filename:
                    filename = f"product_{product_id}.jpg"
                
                save_path = os.path.join(base_path, filename)
                local_url = f"/api/images/{filename}"
                
                # 如果文件已存在，跳过下载
                if os.path.exists(save_path):
                    print(f"跳过(已存在): {name}")
                    cursor.execute("UPDATE product SET cover_image = %s WHERE id = %s", (local_url, product_id))
                    updated_count += 1
                    continue
                
                # 下载图片
                print(f"下载: {name} -> {filename}")
                if download_image(cover_image, save_path):
                    cursor.execute("UPDATE product SET cover_image = %s WHERE id = %s", (local_url, product_id))
                    updated_count += 1
                    print(f"  ✓ 成功")
                else:
                    print(f"  ✗ 失败")
                    
            except Exception as e:
                print(f"处理产品时出错 {name}: {e}")
        
        conn.commit()
        print(f"\n完成！更新了 {updated_count} 个产品的图片路径")
        print(f"图片保存在: {base_path}")
        
    except Exception as e:
        print(f"数据库连接失败: {e}")
        print("请确保已安装 pymysql: pip install pymysql")
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    main()
