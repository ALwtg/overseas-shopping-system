package com.daigou.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);
    
    @Autowired private JwtInterceptor jwtInterceptor;
    
    @Value("${file.upload-path:E:/vscode_workspace/基于 SpringBoot+Vue 的海外物品代购系统的设计与实现/images/}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("图片上传路径配置: {}", uploadPath);
        log.info("========================================");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/images/**", "/uploads/**", "/auth/**", "/product/**", "/category/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以斜杠结尾
        String path = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        
        // 转换为file协议URL（Windows路径需要特殊处理）
        String filePath;
        if (path.startsWith("E:/") || path.startsWith("e:/")) {
            // Windows绝对路径转换为file协议
            filePath = "file:///" + path.replace("\\", "/");
        } else {
            filePath = "file:" + path;
        }
        
        log.info("静态资源映射:");
        log.info("  Handler: /uploads/** -> Location: {}", filePath);
        log.info("  Handler: /api/images/** -> Location: {}", filePath);
        
        // 上传文件资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(filePath)
                .setCachePeriod(3600);
        // 本地图片资源映射
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations(filePath)
                .setCachePeriod(3600);
        
        log.info("静态资源映射完成");
    }
}
