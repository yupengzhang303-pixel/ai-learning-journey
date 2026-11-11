package com.zjjxy.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ai")
public class HealthController {

    private final ChatClient chatClient;

    // 构造器注入（推荐）
    public HealthController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ping")
    public String ping() {
        // 用一句简单的 Prompt 测试 ChatClient 是否装配成功
        return chatClient.prompt()
                .user("用一句话介绍杭州")
                .call()
                .content();
    }
}