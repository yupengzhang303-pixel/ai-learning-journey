# Spring AI 全栈学习合集

14 个子项目组成的 Spring AI 工程学习路径：从大模型 HTTP 调用 → ChatClient → Function Calling → MCP 协议 → Agentic Workflow → Redis 缓存与分布式锁 → 舆情分析落库与 N8N 集成。模型统一使用 Ollama + qwen2.5 本地部署。

## 技术栈

Spring Boot 3.4.5 / 4.1.1 · Java 21 · Spring AI 1.0.x / 2.0.x · MCP · WebFlux(SSE) · Redis + Redisson · MyBatis-Plus · MySQL · N8N Webhook

## 子项目地图

| 子项目 | 主题 |
|--------|------|
| spring-ai / test01 | 大模型 HTTP 调用入门、ChatClient 基础 |
| pro-demo05 | 同步/SSE 流式对话、结构化输出（.entity()）、Redis 会话记忆、Token Advisor |
| funcation-call | Function Calling 实战（售后订单/物流/退款多工具联动）+ Java 反射/动态代理手写工具分发器 |
| mcp-server / mcp-client | MCP 协议：SSE 端点、@Tool 工具注解、自定义 Bearer 鉴权 Filter、远程工具调用 |
| workflow-demo | Agentic Workflow：情感判断 → 风险分级 → LLM 条件路由 |
| pro-demo3-redis | 缓存击穿防护（setIfAbsent 分布式锁 + 双重检查）、布隆过滤器、Redisson |
| sentiments | 舆情分析全链路：防重落库、租户隔离、N8N Webhook 告警 |
| parse-client-demo | WebClient 调用 Python FastAPI 文档解析服务（跨语言协作） |

## 运行

每个子项目独立运行，需本地 Ollama（qwen2.5）：

```bash
ollama pull qwen2.5:3b
cd <子项目>
mvn spring-boot:run
```

## 面试要点

1. MCP 协议是什么：解决什么问题、与 Function Calling 的区别
2. Function Calling 底层原理：为什么能自己写反射 + 动态代理实现工具分发
3. Redis 三件套：缓存穿透（布隆过滤器）、击穿（互斥锁）、雪崩（过期时间随机化）
4. Agent 编排与工作流设计：条件路由 vs 循环 vs 人工介入
