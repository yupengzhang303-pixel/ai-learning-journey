package com.zjjxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkflowDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowDemoApplication.class, args);
        System.out.println("====== 智能工作流启动成功 ======");
        System.out.println("测试接口: http://localhost:8080/api/v1/agent/sentiment?content=这个产品太糟糕了，我要投诉");
    }
}