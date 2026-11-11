package com.zjjxy.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class TokenAdvisor implements CallAdvisor {

    // 累计输入Token数量（线程安全）
    private final AtomicLong totalInputTokens = new AtomicLong();
    // 累计输出Token数量
    private final AtomicLong totalOutputTokens = new AtomicLong();

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain chain) {
        // 1. 请求之前做的事情（修改Prompt等）
        System.out.println("接收到调用请求，请求的资源===");

        // 2. 放行进入调用阶段，调用结束得到响应内容
        ChatClientResponse response = chain.nextCall(chatClientRequest);

        // 3. 响应之前 做Token统计
        ChatResponse chatResponse = response.chatResponse(); // 取出模型真实的响应

        // 健壮性判断
        if (chatResponse != null && chatResponse.getMetadata() != null) {
            Usage usage = chatResponse.getMetadata().getUsage(); // 取出usage字段
            if (usage != null) {
                // 获取输入、输出token，并计入累计
                totalInputTokens.addAndGet(usage.getPromptTokens());
                totalOutputTokens.addAndGet(usage.getCompletionTokens());
                System.out.println("本次消耗输入Token: " + usage.getPromptTokens() + ", 输出Token: " + usage.getCompletionTokens());
            }
        }
        System.out.println("响应处理===");
        return response;
    }

    @Override
    public String getName() {
        return "token_cal";
    }

    @Override
    public int getOrder() {
        return 0; // 数字越小执行越靠前
    }

    // 对外暴露累计值的接口，Token看板接口要用
    public long getTotalInputTokens() {
        return totalInputTokens.get();
    }

    public long getTotalOutputTokens() {
        return totalOutputTokens.get();
    }
}