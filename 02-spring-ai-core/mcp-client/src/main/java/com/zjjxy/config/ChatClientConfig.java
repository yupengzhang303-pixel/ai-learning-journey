package com.zjjxy.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider mcpToolProvider) {
        System.out.println("【MCP Client】挂载远程工具到 ChatClient...");
        return builder
                .defaultToolCallbacks(mcpToolProvider)
                .build();
    }
}