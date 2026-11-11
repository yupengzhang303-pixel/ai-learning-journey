package com.zjjxy.service;

import com.zjjxy.config.SensitiveWordsProperties;
import com.zjjxy.filter.SensitiveWordFilter;
import com.zjjxy.prompt.CustomerServicePrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CustomerServiceService {

    private final ChatClient chatClient;
    private final SensitiveWordFilter sensitiveWordFilter;
    private final SensitiveWordsProperties sensitiveWordsProperties;

    // 模拟知识库
    private static final String KNOWLEDGE = """
            1. 年假：入职满一年5天年假，随工龄每年递增1天，上限15天；
            2. 退款：支持7天无理由退款
            3. 发票：付款后支持在线申请电子发票
            """;

    // 构造器注入
    public CustomerServiceService(ChatClient chatClient,
                                  SensitiveWordFilter sensitiveWordFilter,
                                  SensitiveWordsProperties sensitiveWordsProperties) {
        this.chatClient = chatClient;
        this.sensitiveWordFilter = sensitiveWordFilter;
        this.sensitiveWordsProperties = sensitiveWordsProperties;
    }

    // 同步对话
    public String chat(String question) {
        String strategy = sensitiveWordsProperties.getStrategy();
        System.out.println("[CustomerServiceService] 收到问题: " + question + "，策略: " + strategy);
        // 敏感词过滤
        SensitiveWordFilter.Strategy s = SensitiveWordFilter.Strategy.valueOf(strategy.toUpperCase());
        SensitiveWordFilter.FilterResult result = sensitiveWordFilter.filter(question, s);
        if (!result.passed()) {
            System.out.println("[CustomerServiceService] 敏感词拦截，返回: " + result.message());
            return result.message();
        }

        String system = CustomerServicePrompt.SYSTEM_PROMPT
                .replace("{knowledge}", KNOWLEDGE);
        return chatClient.prompt()
                .system(system)
                .user(question)
                .call()
                .content();
    }

    // 流式对话 + 多轮记忆
    public Flux<String> chatStreamWithMemory(String conversationId, String question) {
        String strategy = sensitiveWordsProperties.getStrategy();
        System.out.println("[CustomerServiceService] 流式收到问题: " + question + "，策略: " + strategy);
        // 敏感词过滤
        SensitiveWordFilter.Strategy s = SensitiveWordFilter.Strategy.valueOf(strategy.toUpperCase());
        SensitiveWordFilter.FilterResult filterResult = sensitiveWordFilter.filter(question, s);
        if (!filterResult.passed()) {
            System.out.println("[CustomerServiceService] 流式敏感词拦截，返回: " + filterResult.message());
            return Flux.just(filterResult.message());
        }

        String system = CustomerServicePrompt.SYSTEM_PROMPT
                .replace("{knowledge}", KNOWLEDGE);

        return chatClient.prompt()
                .system(system)
                .user(question)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .stream()
                .content();
    }


}
