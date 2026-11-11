package com.zjjxy.controller;

import com.zjjxy.service.SpringAiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/v1/ollama")
public class SpringAiController {

    private final SpringAiService aiService;

    public SpringAiController(SpringAiService aiService) {
        this.aiService = aiService;
    }

    // 同步调用
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRecord chatRecord) {
        return aiService.chat(chatRecord.message());
    }

    // 流式调用（声明返回 SSE 流，前端用 EventSource 接收）
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody ChatRecord chatRecord) {
        return aiService.stream(chatRecord.message());
    }
}