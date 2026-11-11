package com.zjjxy.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/after_sales")
public class AfterSaleController {

    private final ChatClient afterSaleChatClient;

    // 注意：因为项目里有多个 ChatClient Bean，必须用 @Qualifier 明确指定
    public AfterSaleController(@Qualifier("afterSaleChatClient") ChatClient afterSaleChatClient) {
        this.afterSaleChatClient = afterSaleChatClient;
    }

    @PostMapping
    public String afterSale(@RequestBody Map<String, String> body) {
        return afterSaleChatClient.prompt()
                .user(body.get("message"))
                .call()
                .content();
    }
}