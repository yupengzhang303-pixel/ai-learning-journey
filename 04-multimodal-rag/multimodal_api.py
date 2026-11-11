import os
import json
import requests
from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
from elasticsearch import Elasticsearch

load_dotenv()  # 从项目根目录 .env 读取密钥（参考 .env.example）

# ==========================================
# 1. 初始化配置与模型
# ==========================================
app = FastAPI(title="多模态 RAG 问答服务")

# 允许跨域（方便前端调用）
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# 挂载静态文件目录，让浏览器能直接访问到你 data/images 下的图片！
# 这样前端就可以直接通过 http://localhost:8000/images/xxx.png 看到图片
os.makedirs("data/images", exist_ok=True)
app.mount("/images", StaticFiles(directory="data/images"), name="images")

print("正在加载 BGE 和 CLIP 模型...")
bge_model = SentenceTransformer("BAAI/bge-small-zh-v1.5")
clip_model = SentenceTransformer("clip-ViT-B-32")

# 连接你的 ES，凭据从环境变量/.env 读取
es = Elasticsearch(
    ["https://localhost:9200"],
    basic_auth=("elastic", os.getenv("ES_PASSWORD", "")),
    ca_certs=os.getenv("ES_CA_CERTS", "")
)

# DeepSeek API 配置
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY", "")
DEEPSEEK_URL = "https://api.deepseek.com/chat/completions"


# ==========================================
# 2. 核心检索与融合逻辑
# ==========================================
class QueryReq(BaseModel):
    query: str
    top_k: int = 3


def bm25_search(query, top_k=10):
    resp = es.search(index="multimodal_docs", body={
        "query": {"match": {"content": query}}, "size": top_k
    })
    return [(hit["_source"]["content"], hit["_source"].get("image_path", "")) for hit in resp["hits"]["hits"]]


def bge_search(query, top_k=10):
    vec = bge_model.encode([query], normalize_embeddings=True)[0].tolist()
    resp = es.search(index="multimodal_docs", body={
        "knn": {"field": "bge_vector", "query_vector": vec, "k": top_k, "num_candidates": top_k * 10}
    })
    return [(hit["_source"]["content"], hit["_source"].get("image_path", "")) for hit in resp["hits"]["hits"]]


def clip_search(query, top_k=10):
    vec = clip_model.encode([query], normalize_embeddings=True)[0].tolist()
    resp = es.search(index="multimodal_docs", body={
        "knn": {"field": "clip_vector", "query_vector": vec, "k": top_k, "num_candidates": top_k * 10}
    })
    return [(hit["_source"]["content"], hit["_source"].get("image_path", "")) for hit in resp["hits"]["hits"]]


def rrf_fusion(results_list, k=60):
    """RRF 算法：多路结果融合"""
    scores = {}
    doc_info = {}
    for results in results_list:
        for rank, (text, img_path) in enumerate(results):
            scores[text] = scores.get(text, 0) + 1.0 / (k + rank + 1)
            doc_info[text] = img_path

    # 按分数降序排序
    sorted_results = sorted(scores.items(), key=lambda x: -x[1])
    return [(text, doc_info[text], score) for text, score in sorted_results]


# ==========================================
# 3. 问答接口
# ==========================================
@app.post("/ask")
def ask(req: QueryReq):
    query = req.query
    print(f"收到提问: {query}")

    # 1. 三路并行检索
    bm25_res = bm25_search(query, 10)
    bge_res = bge_search(query, 10)
    clip_res = clip_search(query, 10)

    # 2. RRF 融合
    fused_res = rrf_fusion([bm25_res, bge_res, clip_res])
    top_results = fused_res[:req.top_k]

    # 3. 组装上下文
    context = ""
    sources = []
    for text, img_path, score in top_results:
        context += f"- {text}\n"
        # 提取文件名以便前端拼接 URL
        img_url = ""
        if img_path:
            file_name = os.path.basename(img_path)
            img_url = f"http://localhost:8000/images/{file_name}"

        sources.append({"text": text, "score": score, "image_url": img_url})

    # 4. 调用 DeepSeek 生成回答
    prompt = f"""请严格根据以下【参考资料】回答用户问题。如果资料中没有相关信息，请直接回答“根据现有资料无法回答”，绝不要编造。
【参考资料】：
{context}
【用户问题】：
{query}
【回答】："""

    headers = {
        "Authorization": f"Bearer {DEEPSEEK_API_KEY}",
        "Content-Type": "application/json"
    }
    data = {
        "model": "deepseek-chat",
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.1
    }

    try:
        resp = requests.post(DEEPSEEK_URL, headers=headers, json=data)
        answer = resp.json()['choices'][0]['message']['content']
    except Exception as e:
        answer = f"调用大模型失败: {e}"

    return {"answer": answer, "sources": sources}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)