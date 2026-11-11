package com.zjjxy;

import org.springframework.ai.mcp.client.autoconfigure.McpClientAutoConfiguration;
import org.springframework.ai.mcp.client.autoconfigure.McpToolCallbackAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        McpClientAutoConfiguration.class,           // 禁掉自动创建 McpSyncClient
        McpToolCallbackAutoConfiguration.class      // 禁掉自动装配 ToolCallbackProvider
})
public class McpClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }
}