package com.zjjxy.sentiments.controller; // 修正了包名

import com.zjjxy.sentiments.dto.RiskDataDto; // 修正了包名
import com.zjjxy.sentiments.entity.SentimentRecord; // 修正了包名
import com.zjjxy.sentiments.service.SentimentService; // 修正了包名
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sentiment")
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentService sentimentService;

    @PostMapping("/receive")
    public Map<String, Object> receiveData(@RequestBody RiskDataDto dto) {
        sentimentService.processAndSave(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "success");
        return result;
    }

    // 【新增】查询指定租户的舆情列表
    @GetMapping("/list")
    public List<SentimentRecord> getList(@RequestParam Long tenantId) {
        return sentimentService.getListByTenant(tenantId);
    }
}