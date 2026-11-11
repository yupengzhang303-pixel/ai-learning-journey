# LLM 安全：Prompt 注入防御 + n8n 编排

FastAPI 情感分析服务：system prompt 规则约束 + 输入隔离的 Prompt 注入防御、强制 JSON 结构化输出、渐进式披露生成舆情日报；mock 脚本模拟 n8n 抓取 → Python 分析 → Java 落库告警 → 钉钉 Webhook 的完整自动化工作流。

## 技术栈

FastAPI · Ollama · n8n 工作流 · Prompt 注入防御 · 结构化输出

## 亮点

- **Prompt 注入防御**：system prompt 规则约束 + `<data>` 标签输入隔离，且带攻击测试用例验证
- **n8n 低代码编排 + Python 服务混编**：体现自动化工作流架构能力
- **结构化输出**：强制 JSON 格式 + 解析兜底

## 运行

```bash
pip install fastapi uvicorn requests
ollama pull qwen2.5:7b
python sentiment_service.py     # 情感分析服务
python mock_n8n_java.py         # 模拟完整工作流（含注入攻击用例）
```

## 面试要点

1. Prompt 注入的常见攻击面与防御手段（输入隔离、指令优先级、输出过滤）
2. LLM 应用安全的评估框架（OWASP LLM Top 10）
3. n8n 这类低代码编排工具与纯代码方案的取舍
