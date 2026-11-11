# RAG 知识库问答全流程

端到端 RAG 系统：PDF/docx/xlsx 多格式解析 → 父子分片 → FAISS + BGE 向量化 → LangChain Retriever + Ollama 生成，严格反幻觉 Prompt；另含 3 个 FastAPI 服务（文档解析 / 切分 / 统计）与手写余弦相似度。

## 技术栈

FastAPI · LangChain · FAISS · Sentence-Transformers（bge-small-zh-v1.5）· Ollama（qwen2.5:7b）· pypdf / python-docx / pandas

## 亮点

- **多格式文档解析**：PDF / Word / Excel 统一解析管道
- **父子分片（Parent-Child）**：小切片检索、大切片喂 LLM，兼顾召回精度与上下文完整
- **反幻觉 Prompt**：严格约束"知识库没有就回答不知道"
- **3 个独立 FastAPI 服务**：解析、切分、统计微服务化

## 运行

```bash
pip install fastapi uvicorn langchain langchain-text-splitters faiss-cpu \
  sentence-transformers pypdf python-docx pandas requests
ollama pull qwen2.5:7b
python rag_server.py
```

## 面试要点

1. 父子分片为什么有效：检索粒度与上下文长度的权衡
2. RAG 评测：忠实度、答案相关性、上下文召回率怎么量化
3. 反幻觉的工程手段：Prompt 约束、引用溯源、置信度阈值
