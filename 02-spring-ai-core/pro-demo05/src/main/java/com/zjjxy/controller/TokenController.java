package com.zjjxy.controller;

import com.zjjxy.advisor.TokenAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/tokens")
public class TokenController {

    private final TokenAdvisor tokenAdvisor;
    private final ChatClient tokenChatClient;

    // 构造器注入
    public TokenController(TokenAdvisor tokenAdvisor, ChatClient tokenChatClient) {
        this.tokenAdvisor = tokenAdvisor;
        this.tokenChatClient = tokenChatClient;
    }

    // 查询累计Token消耗情况
    @GetMapping("/cat")
    public Map<String, Object> tokenCat() {
        long input = tokenAdvisor.getTotalInputTokens();
        long output = tokenAdvisor.getTotalOutputTokens();
        return Map.of(
                "input_token", input,       // 输入token
                "output_token", output,     // 输出token
                "total_tokens", input + output // 总计
        );
    }

    // 触发一次真实调用，观察token数值变化
    @GetMapping("/test")
    public String onceTest() {
        return tokenChatClient.prompt()
                .user("介绍一下Spring AI")
                .call()
                .content();
    }
}