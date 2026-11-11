package com.zjjxy.proxy.impl;

import com.zjjxy.proxy.ChatService;

public class ChatServiceImpl implements ChatService {

    @Override
    public String chat(String message) {
        // 模拟调用 AI
        return "AI模型回答：" + message;
    }
}