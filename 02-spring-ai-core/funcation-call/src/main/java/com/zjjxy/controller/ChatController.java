package com.zjjxy.controller;

import com.zjjxy.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 对话接口
     * POST /api/v1/tools/chat
     * Body: {"message": "杭州的天气如何"}
     */
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatService.chat(request.message());
    }

    // 请求体
    public record ChatRequest(String message) {}
}