package com.zjjxy.controller;

import com.zjjxy.common.Result;
import com.zjjxy.service.CustomerServiceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/customer-service")
public class CustomerServiceController {

    private final CustomerServiceService cService;

    public CustomerServiceController(CustomerServiceService cService) {
        this.cService = cService;
    }

    // 健康检查
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // 同步对话
    @PostMapping("/chat")
    public Result<ChatReply> chat(@RequestBody ChatRequest request) {
        String reply = cService.chat(request.question());
        return Result.ok(new ChatReply(reply));
    }

    // 流式对话（SSE）+ 多轮记忆
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return cService.chatStreamWithMemory(
                request.conversationId(),
                request.question()
        );
    }

    // 请求体
    public record ChatRequest(String conversationId, String question) {}

    // 响应体
    public record ChatReply(String reply) {}
}