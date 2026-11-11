package com.zjjxy.proxy.impl;

import com.zjjxy.proxy.ChatService;

/**
 * 静态代理：手动写一个包装类，加日志
 * 缺点：每个接口都要写一个代理类
 */
public class ChatServiceImpl2 implements ChatService {

    private final ChatService target = new ChatServiceImpl();

    @Override
    public String chat(String message) {
        System.out.println("【静态代理】开始调用，参数：" + message);
        String result = target.chat(message);
        System.out.println("【静态代理】结束调用，结果：" + result);
        return result;
    }
}