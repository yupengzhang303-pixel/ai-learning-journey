# Spring AI 智能客服系统

基于 Spring AI + Ollama（qwen2.5:3b）的智能客服：同步/SSE 流式双通道对话、Redis 会话记忆、FAQ 快速匹配、敏感词过滤、token 用量统计，配套静态聊天界面。

## 技术栈

Spring Boot 3.4.5 · Java 21 · Spring AI 1.0.0 · Ollama · Redis · WebFlux/SSE · ChatClient Advisor

## 亮点

- **双通道对话**：同步接口 + SSE 流式输出（Flux + TEXT_EVENT_STREAM）
- **自研 Advisor**：实现 CallAdvisor + StreamAdvisor 两个扩展点统计 token 用量
- **敏感词过滤**：Redis Set 存储词库、精确 + 通配符匹配、reject/replace/log 三种策略
- **FAQ 两级匹配**：精确 → 关键词，命中则不走大模型（路由思想，控制成本）

## 运行

```bash
ollama pull qwen2.5:3b   # 需 Redis 运行在 6379
mvn spring-boot:run
```

## 面试要点

1. Spring AI Advisor 扩展机制：在模型调用链路的哪个环节介入
2. SSE vs WebSocket 选型：为什么客服场景用 SSE
3. FAQ 前置路由的收益：减少大模型调用成本与延迟
4. 敏感词过滤的工程细节：通配符匹配算法、策略分层
