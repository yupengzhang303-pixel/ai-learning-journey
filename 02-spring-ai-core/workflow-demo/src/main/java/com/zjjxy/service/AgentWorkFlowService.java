package com.zjjxy.service;

import com.zjjxy.agent.RiskLevelNode;
import com.zjjxy.agent.SentimentNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentWorkFlowService {

    @Autowired
    private SentimentNode sentimentNode;

    @Autowired
    private RiskLevelNode riskLevelNode;

    /**
     * 编排工作流：采集 -> LLM判断 -> 条件分支【节点内嵌LLM决策】
     */
    public String process(String content) {
        // 1. 调用 LLM 进行情感判断
        String sentiment = sentimentNode.analyze(content);
        
        // 2. 根据 LLM 的决策动态路由（Agentic Workflow 核心）
        return switch (sentiment) {
            case "负面" -> {
                String level = riskLevelNode.grade(content);
                yield "【告警】触发告警（" + level + "级），推送给负责人";
            }
            case "正面" -> "【记录】记录为正面舆情";
            default -> "【忽略】中性消息，不处理";
        };
    }
}