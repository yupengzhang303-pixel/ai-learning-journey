package com.zjjxy.controller;

import com.zjjxy.service.TextAssistantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/v1/ai/assistant")
public class TextAssistantController {

    private final TextAssistantService service;

    public TextAssistantController(TextAssistantService service) {
        this.service = service;
    }

    // 同步客服问答
    @PostMapping("/customer-service")
    public String customerService(@RequestBody ChatRecord chatRecord) {
        return service.customerService(chatRecord.message());
    }

    // 翻译
    @PostMapping("/translate")
    public String translate(@RequestBody TranslateReq translateReq) {
        return service.translate(translateReq.text(), translateReq.target());
    }

    // CoT 分类
    @PostMapping("/classify")
    public String classify(@RequestBody ChatRecord chatRecord) {
        return service.classifyByCot(chatRecord.message());
    }

    // 流式客服问答
    @PostMapping(value = "/customer-service/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> customerServiceStream(@RequestBody ChatRecord chatRecord) {
        return service.customerServiceStream(chatRecord.message());
    }
}