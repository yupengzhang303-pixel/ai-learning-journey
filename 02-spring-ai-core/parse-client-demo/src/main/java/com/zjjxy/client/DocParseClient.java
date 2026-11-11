package com.zjjxy.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class DocParseClient {

    private final WebClient webClient;

    public DocParseClient(@Value("${app.python.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Map<String, Object> parse(String content, int chunkSize) {
        Map<String, Object> reqBody = Map.of(
                "content", content,
                "chunk_size", chunkSize
        );
        return webClient.post()
                .uri("/parse")
                .bodyValue(reqBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
    public Map<String, Object> stats(String text) {
        Map<String, Object> reqBody = Map.of("text", text);
        return webClient.post()
                .uri("/stats")
                .bodyValue(reqBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
