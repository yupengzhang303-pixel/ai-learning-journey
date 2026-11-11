package com.zjjxy.config;

import com.zjjxy.tool.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    /**
     * 把 WeatherTools 注册到 ChatClient
     * 这样模型就能在需要时自动调用 WeatherTools 里的 @Tool 方法
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, WeatherTools weatherTools) {
        return builder
                .defaultTools(weatherTools)   // 注册工具
                .build();
    }
}