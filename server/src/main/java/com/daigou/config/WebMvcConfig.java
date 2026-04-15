package com.daigou.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired private JwtInterceptor jwtInterceptor;
    
    @Value("${file.upload-path:E:/vscode_workspace/基于 SpringBoot+Vue 的海外物品代购系统的设计与实现/images/}")
    private String uploadPath;

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
        // 上传文件资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path);
        // 本地图片资源映射
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:" + path);
    }
}
