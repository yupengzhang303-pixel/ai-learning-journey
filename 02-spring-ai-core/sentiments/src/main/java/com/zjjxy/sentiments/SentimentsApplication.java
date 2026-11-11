package com.zjjxy.sentiments;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zjjxy.sentiments.mapper") // 扫描 Mapper 接口
public class SentimentsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentimentsApplication.class, args);
    }
}