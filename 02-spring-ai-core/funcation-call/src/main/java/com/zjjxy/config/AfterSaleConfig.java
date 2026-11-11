package com.zjjxy.config;

import com.zjjxy.tool.AfterSaleTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AfterSaleConfig {

    /**
     * 售后专用的 ChatClient，注册 4 个售后工具
     * Bean 名：afterSaleChatClient
     */
    @Bean
    public ChatClient afterSaleChatClient(ChatClient.Builder builder,
                                          AfterSaleTools saleTools) {
        return builder
                .defaultTools(saleTools)
                .build();
    }
}