package com.zjjxy.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/memory")
public class MemoryTestController {

    private final ChatClient memoryChatClient;

    // 注意：这里注入的是刚才配置好的 memoryChatClient
    public MemoryTestController(ChatClient memoryChatClient) {
        this.memoryChatClient = memoryChatClient;
    }

    @PostMapping("/test")
    public String testMemory(@RequestBody Map<String, String> body) {
        String cid = body.get("cid"); // 模拟同一个对话通过前端传递的cid
        String msg = body.get("msg");
        return memoryChatClient.prompt()
                .user(msg)
                // ⚠️ 极其重要：下面这个字符串必须一字不差，写错静默失效！
                .advisors(a -> a.param("chat_memory_conversation_id", cid))
                .call()
                .content();
    }
}