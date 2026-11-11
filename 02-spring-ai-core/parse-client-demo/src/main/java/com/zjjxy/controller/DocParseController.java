package com.zjjxy.controller;

import com.zjjxy.client.DocParseClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DocParseController {

    private final DocParseClient docParseClient;

    public DocParseController(DocParseClient docParseClient) {
        this.docParseClient = docParseClient;
    }

    /**
     * 转发到 Python 的 /parse
     * POST http://localhost:8080/api/v1/parse
     * Body: {"content": "长文本...", "chunk_size": 500}
     */
    @PostMapping("/parse")
    public Map<String, Object> parse(@RequestBody Map<String, Object> body) {
        String content = (String) body.get("content");
        Integer chunkSize = (Integer) body.getOrDefault("chunk_size", 500);
        return docParseClient.parse(content, chunkSize);
    }
    @PostMapping("/stats")
    public Map<String, Object> stats(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        return docParseClient.stats(text);
    }
}