package com.zjjxy.controller;

import com.zjjxy.client.LlmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/prompt")
public class PromptController {

    @Autowired
    private LlmClient llmClient;

    // 同一句 message，用不同的 Prompt 技巧生成，对比效果
    @PostMapping("/compare")
    public Map<String, String> comparePrompt(@RequestBody ChatRecord record) {
        String question = record.message();

        // LinkedHashMap：保持插入顺序，前端看对比结果不会乱序
        // （HashMap 不保证顺序；LinkHashMap 是拼写错误，正确是 LinkedHashMap）
        Map<String, String> result = new LinkedHashMap<>();

        // v1: 没有任何提示
        result.put("v1_没有任何提示",
                llmClient.chatOnce("回答：" + question));

        // v2: 加角色
        result.put("v2_加角色",
                llmClient.chatOnce("你是一位资深客服，请回答：" + question));

        // v3: 结构化 + 加角色
        result.put("v3_结构化",
                llmClient.chatOnce(
                        "<角色>资深客服</角色>" +
                        "<任务>回答用户问题</任务>" +
                        "<问题>" + question + "</问题>"
                ));

        // v4: CoT 思维链
        result.put("v4_思维链",
                llmClient.chatOnce("请先分析问题，再逐步给出答案：" + question));

        return result;
    }
}