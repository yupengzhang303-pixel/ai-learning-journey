package com.zjjxy.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SpringAiService {

    private final ChatClient chatClient;

    // 构造器注入（推荐）
    public SpringAiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 1. 同步调用，一次性拿到所有结果
    public String chat(String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    // 2. 带 System 角色的调用【设定人设】
    public String chatWithSystem(String system, String message) {
        return chatClient
                .prompt()
                .system(system) // 系统消息：设定角色/规则
                .user(message)
                .call()
                .content();
    }

    // 3. 流式调用：Flux<String> 每个元素是一段文字
    public Flux<String> stream(String message) {
        return chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }
}