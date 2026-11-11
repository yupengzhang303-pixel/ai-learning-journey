package com.zjjxy.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RiskLevelNode {

    @Autowired
    private ChatClient chatClient;

    /**
     * 负面舆情进行分级：高/中/低
     */
    public String grade(String content) {
        return chatClient.prompt()
                .user("下面是一条判定为负面的舆情，评估其严重程度，只返回一个词：高/中/低。舆情：" + content)
                .call()
                .content().trim();
    }
}