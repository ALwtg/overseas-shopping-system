package com.daigou.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@ControllerAdvice
public class StaticResourceErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceErrorHandler.class);
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e, HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // 只记录静态资源相关的错误
        if (uri.contains("/api/images/") || uri.contains("/uploads/")) {
            log.error("========================================");
            log.error("静态资源访问错误!");
            log.error("URI: {}", uri);
            log.error("请求方法: {}", request.getMethod());
            log.error("错误类型: {}", e.getClass().getName());
            log.error("错误信息: {}", e.getMessage());
            
            // 尝试解析请求的图片路径
            String imageName = uri.substring(uri.lastIndexOf("/") + 1);
            log.error("请求的图片: {}", imageName);
            
            // 检查图片目录
            String uploadPath = "E:/vscode_workspace/基于 SpringBoot+Vue 的海外物品代购系统的设计与实现/images/";
            File imageDir = new File(uploadPath);
            log.error("图片目录存在: {}", imageDir.exists());
            log.error("图片目录可读: {}", imageDir.canRead());
            
            if (imageDir.exists()) {
                File[] files = imageDir.listFiles();
                log.error("目录中文件数量: {}", files != null ? files.length : 0);
                if (files != null && files.length > 0) {
                    log.error("目录中文件列表:");
                    for (File f : files) {
                        log.error("  - {} (大小: {} bytes)", f.getName(), f.length());
                    }
                }
                
                // 检查特定图片是否存在
                File targetImage = new File(uploadPath + imageName);
                log.error("目标图片存在: {}", targetImage.exists());
                if (targetImage.exists()) {
                    log.error("目标图片可读: {}", targetImage.canRead());
                    log.error("目标图片大小: {} bytes", targetImage.length());
                }
            }
            
            // 打印异常堆栈
            log.error("异常堆栈:", e);
            log.error("========================================");
        }
    }
}
