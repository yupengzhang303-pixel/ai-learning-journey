# LangGraph 多智能体工作流

基于 LangGraph + Ollama 的四种 Agent 架构实验：StateGraph 条件路由的舆情分级工作流、Plan-Execute 规划执行、Reflection 自我反思迭代、手写 ReAct Agent（工具注册表 + 正则解析 + 兜底容错）。

## 技术栈

LangGraph · Ollama（qwen2.5:7b）· Agent 架构（ReAct / Plan-Execute / Reflection）· 状态机

## 亮点

- **LangGraph 状态机建模**：条件边 + 状态路由，把 Agent 流程显式化
- **手写 ReAct**：JSON 解析容错、工具兜底，不是照抄教程
- **四种架构横向对比**：同一问题用不同 Agent 模式实现，适合讲架构选型

## 运行

```bash
pip install langgraph langchain-ollama
ollama pull qwen2.5:7b
python teact_agent.py          # 手写 ReAct
python langgraph_report.py     # 条件路由工作流
python plan_execute_agent.py   # 规划-执行
python reflection_agent.py     # 反思迭代
```

## 面试要点

1. ReAct vs Plan-Execute vs Reflection 各自的适用场景与成本差异
2. LangGraph 的 State / Node / Edge 模型
3. LLM 输出解析的容错设计：正则 + JSON 解析 + 兜底重试
