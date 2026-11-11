package com.zjjxy.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Token 统计拦截器
 * 同时实现 CallAdvisor（同步）+ StreamAdvisor（流式）
 */
@Component
public class TokenStatAdvisor implements CallAdvisor, StreamAdvisor {

    private final AtomicLong totalInputTokens = new AtomicLong(0);
    private final AtomicLong totalOutputTokens = new AtomicLong(0);

    // ================= 同步调用 =================
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientResponse response = chain.nextCall(request);
        System.out.println("===== [同步] TokenStatAdvisor 被调用 =====");
        try {
            // 从 usage 读（可能为 null）
            var usage = response.chatResponse() != null
                    && response.chatResponse().getMetadata() != null
                    ? response.chatResponse().getMetadata().getUsage() : null;

            if (usage != null && usage.getPromptTokens() != null
                    && usage.getCompletionTokens() != null) {
                totalInputTokens.addAndGet(usage.getPromptTokens());
                totalOutputTokens.addAndGet(usage.getCompletionTokens());
                System.out.println("同步：真实 usage input=" + usage.getPromptTokens()
                        + ", output=" + usage.getCompletionTokens());
            } else {
                String userMsg = request.prompt().getContents();
                String aiMsg = response.chatResponse() != null
                        && response.chatResponse().getResult() != null
                        ? response.chatResponse().getResult().getOutput().getText() : "";
                long i = estimateTokens(userMsg);
                long o = estimateTokens(aiMsg);
                totalInputTokens.addAndGet(i);
                totalOutputTokens.addAndGet(o);
                System.out.println("同步：估算 input=" + i + ", output=" + o);
            }
        } catch (Exception e) {
            System.err.println("同步 Token 统计失败: " + e.getMessage());
        }
        return response;
    }

    // ================= 流式调用（重点） =================
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        System.out.println("===== [流式] TokenStatAdvisor 被调用 =====");

        // 用户输入
        String userMsg = request.prompt().getContents();
        long inputEst = estimateTokens(userMsg);

        // 累积 AI 输出
        StringBuilder aiOutput = new StringBuilder();

        return chain.nextStream(request)
                .doOnNext(resp -> {
                    // 每个 chunk 拼起来
                    if (resp.chatResponse() != null
                            && resp.chatResponse().getResult() != null
                            && resp.chatResponse().getResult().getOutput() != null) {
                        String text = resp.chatResponse().getResult().getOutput().getText();
                        if (text != null) aiOutput.append(text);
                    }
                })
                .doOnComplete(() -> {
                    // 流结束，统一统计
                    long outputEst = estimateTokens(aiOutput.toString());
                    totalInputTokens.addAndGet(inputEst);
                    totalOutputTokens.addAndGet(outputEst);
                    System.out.println("流式：统计完成 input=" + inputEst
                            + ", output=" + outputEst
                            + ", aiTextLength=" + aiOutput.length());
                })
                .doOnError(err -> System.err.println("流式异常: " + err.getMessage()));
    }

    /**
     * 估算 Token 数：中文 1 字 ≈ 2 token
     */
    private long estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        long chineseCount = text.chars()
                .filter(c -> c >= 0x4E00 && c <= 0x9FFF)
                .count();
        long otherCount = text.length() - chineseCount;
        return chineseCount * 2 + Math.max(otherCount / 2, 1);
    }

    @Override
    public String getName() {
        return "token-stat-advisor";
    }

    @Override
    public int getOrder() {
        return 100;
    }

    public long getTotalInputTokens() {
        return totalInputTokens.get();
    }

    public long getTotalOutputTokens() {
        return totalOutputTokens.get();
    }

    public long getTotalTokens() {
        return totalInputTokens.get() + totalOutputTokens.get();
    }
}