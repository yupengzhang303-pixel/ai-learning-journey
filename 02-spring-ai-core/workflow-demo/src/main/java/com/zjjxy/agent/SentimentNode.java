package com.zjjxy.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SentimentNode {

    @Autowired
    private ChatClient chatClient;

    /**
     * 情感判断：让 LLM 自主决策消息是正面/负面/中性
     */
    public String analyze(String content) {
        return chatClient.prompt()
                .user("判断下面这条舆情消息的情感倾向，只返回一个词：正面/负面/中性。消息是：" + content)
                .call()
                .content().trim();
    }
}