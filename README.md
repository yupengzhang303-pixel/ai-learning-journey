# 🚀 AI 学习路程（AI Learning Journey）

从 Java 后端工程到 AI 应用开发的完整学习路径。仓库按学习阶段组织，每个目录是一个可独立运行的项目，覆盖：企业级后端 → Spring AI → 智能客服 → 多模态 RAG → 检索增强 → Agent 架构 → LLM 安全。

## 📚 学习路线图

```
阶段一  企业级 Java 后端 ──► 01-java-saas          SaaS 多租户知识库
                              （Spring Boot 3 + MyBatis-Plus + Docker）

阶段二  Java 侧 AI 工程 ──► 02-spring-ai-core       Spring AI 全栈：MCP / Function Calling / Agentic Workflow
                              （14 个子项目，从裸 HTTP 到 Agent 编排）
                           └► 03-ai-customer-service Spring AI 智能客服：SSE 流式 + Redis 记忆 + 敏感词过滤

阶段三  Python 侧 AI 工程 ─► 04-multimodal-rag      多模态 RAG：CLIP+BGE 双向量、RRF 混合检索
                           └► 05-rag-pipeline       RAG 全流程：文档解析 → 父子分片 → FAISS → 反幻觉生成
                           └► 06-langgraph-agents   LangGraph 多智能体：ReAct / Plan-Execute / Reflection
                           └► 07-llm-security       LLM 安全：Prompt 注入防御 + n8n 工作流编排
```

## 🗂️ 项目一览

| 编号 | 项目 | 语言/框架 | 核心主题 |
|------|------|-----------|----------|
| 01 | [java-saas](01-java-saas/) | Java / Spring Boot 3 | SaaS 多租户、工程化规范 |
| 02 | [spring-ai-core](02-spring-ai-core/) | Java / Spring AI | MCP 协议、Function Calling、Agent |
| 03 | [ai-customer-service](03-ai-customer-service/) | Java / Spring AI | 流式对话、Redis 会话记忆 |
| 04 | [multimodal-rag](04-multimodal-rag/) | Python / FastAPI | 跨模态检索、RRF 融合 |
| 05 | [rag-pipeline](05-rag-pipeline/) | Python / LangChain | RAG 全链路、父子分片 |
| 06 | [langgraph-agents](06-langgraph-agents/) | Python / LangGraph | 多智能体架构设计 |
| 07 | [llm-security](07-llm-security/) | Python / FastAPI | Prompt 注入防御、自动化编排 |

## 🛠️ 技术栈总览

- **后端**：Spring Boot 3.4/4.x · MyBatis-Plus · Redis · MySQL · Docker
- **AI 框架**：Spring AI 1.x/2.x · LangChain · LangGraph · MCP 协议
- **模型**：Ollama 本地部署（qwen2.5）· DeepSeek API · BGE / CLIP 向量模型
- **检索**：Elasticsearch kNN · FAISS · Milvus · BM25 + 向量混合检索（RRF）
- **Python**：FastAPI · Sentence-Transformers · EasyOCR
- **安全**：Prompt 注入防御 · 敏感词过滤 · 密钥环境变量管理

## 🎯 面试考点对照

| 考点 | 对应项目 |
|------|----------|
| MCP 协议 / Agent 工具调用 | 02-spring-ai-core |
| Function Calling 底层原理 | 02-spring-ai-core |
| Redis 缓存击穿 / 分布式锁 / 布隆过滤器 | 02-spring-ai-core |
| 多租户隔离与防越权 | 01-java-saas |
| RAG 全链路与混合检索（RRF） | 04-multimodal-rag / 05-rag-pipeline |
| 父子分片 / 反幻觉设计 | 05-rag-pipeline |
| Agent 架构（ReAct / Plan-Execute / Reflection） | 06-langgraph-agents |
| Prompt 注入防御 / LLM 安全 | 07-llm-security |
| SSE 流式输出 / Advisor 扩展机制 | 03-ai-customer-service |

## ⚠️ 使用说明

- 各项目凭据统一用 `.env` 管理（复制各目录下的 `.env.example` 为 `.env` 填入真实值），严禁硬编码
- 本地模型统一使用 Ollama + qwen2.5；DeepSeek 仅用于 04 项目的生成端
- 每个子项目目录内均有独立 README，含运行步骤与面试要点
