package com.zjjxy.service;

import com.zjjxy.prompt.PromptTemplates;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TextAssistantService {

    private final ChatClient chatClient;

    // 构造器注入（推荐）
    public TextAssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 知识库文本
    private static final String KNOWLEDGE = """
            1. 年假：入职满一年享受5天年假，逐年+1天，上限15天；
            2. 退款：7天内无理由退款
            3. 发票：付款后可在线申请电子发票
            """;

    // 1. 客服回答（同步）
    public String customerService(String question) {
        String promptCus = PromptTemplates.CUSTOMER_SERVICE
                .replace("{knowledge}", KNOWLEDGE)
                .replace("{question}", question);
        return chatClient.prompt().user(promptCus).call().content();
    }

    // 2. 翻译
    public String translate(String text, String target) {
        String systemTrans = PromptTemplates.TRANSFER.replace("{target}", target);
        return chatClient.prompt().system(systemTrans).user(text).call().content();
    }

    // 3. 情感分类 CoT
    public String classifyByCot(String message) {
        String promptCot = PromptTemplates.COT_CLASSIFY.replace("{message}", message);
        return chatClient.prompt().user(promptCot).call().content();
    }

    // 4. 分类 Few-Shot
    public String classifyByFewShot(String text) {
        String promptFewShot = PromptTemplates.FEWSHOT_SENTIMENT.replace("{text}", text);
        return chatClient.prompt().user(promptFewShot).call().content();
    }

    // 5. 流式客服回答
    public Flux<String> customerServiceStream(String question) {
        String promptCus = PromptTemplates.CUSTOMER_SERVICE
                .replace("{knowledge}", KNOWLEDGE)
                .replace("{question}", question);
        return chatClient.prompt().user(promptCus).stream().content();
    }
}