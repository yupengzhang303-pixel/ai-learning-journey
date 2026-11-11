package com.zjjxy.controller;

import com.zjjxy.advisor.TokenStatAdvisor;
import com.zjjxy.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-service")
public class TokenController {

    private final TokenStatAdvisor tokenStatAdvisor;

    public TokenController(TokenStatAdvisor tokenStatAdvisor) {
        this.tokenStatAdvisor = tokenStatAdvisor;
    }

    /**
     * 查询累计 Token 消耗
     * GET /api/v1/customer-service/token-stat
     */
    @GetMapping("/token-stat")
    public Result<Map<String, Object>> tokenStat() {
        long input = tokenStatAdvisor.getTotalInputTokens();
        long output = tokenStatAdvisor.getTotalOutputTokens();
        long total = tokenStatAdvisor.getTotalTokens();

        return Result.ok(Map.of(
                "inputTokens", input,
                "outputTokens", output,
                "totalTokens", total
        ));
    }
}