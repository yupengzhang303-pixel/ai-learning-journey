package com.zjjxy.config;

import com.zjjxy.advisor.TokenAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfig {

    @Bean
    public ChatClient tokenChatClient(ChatClient.Builder builder, TokenAdvisor tokenAdvisor) {
        return builder
                .defaultAdvisors(tokenAdvisor)
                .build();
    }
}