package com.zjjxy.service;

import com.zjjxy.dto.IntentResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class StructuredService {

    private final ChatClient chatClient;

    public StructuredService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 让模型分析意图，并强制返回结构化对象
    public IntentResult analyzeIntent(String message) {
        IntentResult result = chatClient.prompt()
                .user("""
                        分析用户的消息意图，返回JSON，字段：
                        intent（咨询/投诉/建议之一）、
                        summary（一句话概括）、
                        confidence（置信度）
                        消息：%s
                        """.formatted(message))
                .call()
                .entity(IntentResult.class); // 自动反序列化为 IntentResult
        
        return result;
    }
}