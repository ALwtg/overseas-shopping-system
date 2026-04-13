package com.daigou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.daigou.mapper")
public class DaigouApplication {
    public static void main(String[] args) {
        SpringApplication.run(DaigouApplication.class, args);
        System.out.println("============================================");
        System.out.println("  海外物品代购系统后端服务启动成功！端口: 9090");
        System.out.println("============================================");
    }
}
