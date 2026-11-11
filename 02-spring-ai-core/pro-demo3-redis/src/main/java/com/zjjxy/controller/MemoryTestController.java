package com.zjjxy.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/memory")
public class MemoryTestController {

    private final ChatClient memoryChatClient;

    public MemoryTestController(ChatClient memoryChatClient) {
        this.memoryChatClient = memoryChatClient;
    }

    @PostMapping("/test")
    public String test(@RequestBody Map<String, String> body) {
        String cid = body.get("cid");
        String msg = body.get("msg");
        return memoryChatClient.prompt()
                .user(msg)
                .advisors(a -> a.param("chat_memory_conversation_id", cid))
                .call()
                .content();
    }
}