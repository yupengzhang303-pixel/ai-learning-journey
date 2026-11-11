# 多模态 RAG 图文问答系统

文本 + 图像双向量检索的 RAG 系统：BGE 文本向量 + CLIP 图像向量，EasyOCR 提取图片文字，Elasticsearch 双 dense_vector 字段 kNN 检索，BM25 + 双路向量三路检索 RRF 融合，DeepSeek 生成回答，FastAPI 服务化。

## 技术栈

FastAPI · Sentence-Transformers（BGE / CLIP）· Elasticsearch 8（kNN）· EasyOCR · DeepSeek API · RRF 混合检索 · LangChain 切片器

## 亮点

- **跨模态检索**：文本 + 图像双向量模型，支持以文搜图、以图搜图、图文混合问答
- **三路检索 RRF 融合**：BM25 关键词 + BGE 文本向量 + CLIP 图像向量，RRF 排名融合
- **OCR 增强**：EasyOCR 提取图片内文字，弥补纯视觉向量无法理解文字内容的问题
- **服务化**：FastAPI + CORS + 静态图片服务，接近生产形态

## 运行

```bash
pip install -r requirements.txt
cp .env.example .env        # 填入 ES 密码与 DeepSeek Key
# 需本地 ES 8 开启 https + 下载 BGE/CLIP 模型
python multimodal_ingestion.py   # 入库
python multimodal_api.py         # 启动问答服务
```

## 面试要点

1. RRF 融合原理：为什么用排名倒数融合而不是分数加权（不同检索器分数量纲不一致）
2. 为什么图片要先 OCR 再和 CLIP 向量结合
3. ES dense_vector + kNN 与专用向量库（Milvus）的取舍
4. 从本项目到企业级多模态 RAG 还差什么（评测、重排、缓存）
