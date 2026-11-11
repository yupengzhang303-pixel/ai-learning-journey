package com.zjjxy.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mcp")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * GET 版：浏览器地址栏直接测
     * 例：http://localhost:8089/api/v1/mcp/chat?message=杭州天气
     */
    @GetMapping("/chat")
    public String chatGet(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * POST 版：原来的
     */
    @PostMapping("/chat")
    public String chatPost(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .call()
                .content();
    }

    public record ChatRequest(String message) {}
}