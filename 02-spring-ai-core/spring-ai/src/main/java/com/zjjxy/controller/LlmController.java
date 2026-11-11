package com.zjjxy.controller;

import com.zjjxy.client.LlmClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/llm")
public class LlmController {

    private final LlmClient llmClient;

    // 构造器注入，比 @Autowired 字段注入更推荐
    public LlmController(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * 非流式接口：一次性返回完整答案
     * 访问示例：http://localhost:8080/api/v1/llm/chat?message=你好
     */
    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return llmClient.chatOnce(message);
    }

    /**
     * 流式接口：逐字返回，浏览器会像打字机一样显示
     * 访问示例：http://localhost:8080/api/v1/llm/stream?message=你好
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam("message") String message) {
        return llmClient.chatStream(message);
    }
}