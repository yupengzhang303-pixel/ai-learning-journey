package com.zjjxy.controller;

import com.zjjxy.dto.IntentResult;
import com.zjjxy.service.StructuredService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/ai/intent") // 补全路径
public class IntentTestController {

    private final StructuredService structuredService;

    public IntentTestController(StructuredService structuredService) {
        this.structuredService = structuredService;
    }

    @PostMapping("/analyze") // 补全请求方式
    public IntentResult intent(@RequestBody Map<String, String> body) {
        return structuredService.analyzeIntent(body.get("message"));
    }
}