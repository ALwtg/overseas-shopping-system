#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单的图片下载脚本（不依赖数据库）
手动运行后，再执行生成的 SQL 文件
"""
import os
import requests

# 图片列表: (文件名, URL)
images = [
    ("skincare-1.jpg", "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400&h=400&fit=crop"),
    ("skincare-2.jpg", "https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=400&h=400&fit=crop"),
    ("skincare-3.jpg", "https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=400&h=400&fit=crop"),
    ("electronics-1.jpg", "https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=400&h=400&fit=crop"),
    ("electronics-2.jpg", "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400&h=400&fit=crop"),
    ("fashion-1.jpg", "https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=400&h=400&fit=crop"),
    ("fashion-2.jpg", "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop"),
    ("baby-1.jpg", "https://images.unsplash.com/photo-1632054010678-7f2e5a1a7355?w=400&h=400&fit=crop"),
    ("health-1.jpg", "https://images.unsplash.com/photo-1593095948071-474c5cc2c6e9?w=400&h=400&fit=crop"),
    ("health-2.jpg", "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=400&h=400&fit=crop"),
    ("shoes-1.jpg", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop"),
]

base_path = "E:/vscode_workspace/基于 SpringBoot+Vue 的海外物品代购系统的设计与实现/images/"
os.makedirs(base_path, exist_ok=True)

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
}

print("开始下载图片...\n")
for filename, url in images:
    filepath = os.path.join(base_path, filename)
    if os.path.exists(filepath):
        print(f"[SKIP] 已存在: {filename}")
        continue
    
    try:
        response = requests.get(url, headers=headers, timeout=30)
        if response.status_code == 200:
            with open(filepath, 'wb') as f:
                f.write(response.content)
            print(f"[OK] 下载成功: {filename}")
        else:
            print(f"[FAIL] 下载失败: {filename} (状态码: {response.status_code})")
    except Exception as e:
        print(f"[FAIL] 下载失败: {filename} ({e})")

print(f"\n图片保存在: {base_path}")
print("\n请在 MariaDB 中执行 update_images.sql 文件更新图片路径")
