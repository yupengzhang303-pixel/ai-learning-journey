package com.zjjxy.client;

import com.zjjxy.config.LlmProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 客户端类：负责发出 HTTP 请求调用大模型
 */
@Component
public class LlmClient {

    private final WebClient webClient;
    private final LlmProperties llmProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 推荐用构造器注入，避免 @Autowired 字段注入在构造函数中为 null 的问题
     */
    public LlmClient(LlmProperties llmProperties) {
        this.llmProperties = llmProperties;
        this.webClient = WebClient.builder()
                .baseUrl(llmProperties.getBaseUrl())
                .build();
    }

    /**
     * 一次性调用【非流式】
     * 根据用户发送的信息，一次性响应结果
     */
    @SuppressWarnings("unchecked")
    public String chatOnce(String userMessage) {
        // 1. 构造请求体参数
        Map<String, Object> body = Map.of(
                "model", llmProperties.getModel(),
                "messages", List.of(
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", llmProperties.getTemperature(),
                "stream", false
        );

        // 2. 发送请求并响应
        Map<String, Object> resp = webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null) {
            return "【无响应】";
        }

        // 3. 解析响应内容
        List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.get("choices");
        Map<String, Object> first = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) first.get("message");
        return (String) message.get("content");
    }

    /**
     * 从 SSE 的 data JSON 中提取 choices[0].delta.content
     *
     * @param data JSON 字符串
     * @return 增量文本，没有则返回空字符串
     */
    @SuppressWarnings("unchecked")
    public String exactData(String data) {
        try {
            Map<String, Object> json = objectMapper.readValue(data, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) json.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "";
            }
            Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
            if (delta == null) {
                return "";
            }
            Object content = delta.get("content");
            return content == null ? "" : content.toString();
        } catch (Exception e) {
            // 解析失败（比如分片边界问题）就丢弃这一段
            return "";
        }
    }

    /**
     * 流式调用：逐字返回
     */
    public Flux<String> chatStream(String userMessage) {
        // 1. 构造请求体参数
        Map<String, Object> body = Map.of(
                "model", llmProperties.getModel(),
                "messages", List.of(
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", llmProperties.getTemperature(),
                "stream", true
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> line.startsWith("data"))
                .map(line -> line.substring(5).trim())
                .filter(data -> !data.equals("[DONE]"))
                .map(this::exactData)
                .filter(s -> !s.isEmpty());
    }
}