import os
import requests
from dotenv import load_dotenv
from pydantic import BaseModel
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sentence_transformers import SentenceTransformer
from elasticsearch import Elasticsearch

load_dotenv()  # 从项目根目录 .env 读取密钥（参考 .env.example）

app = FastAPI(title="图文混合问答 (老师文档复刻版)")
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

# ==========================================
# 1. 加载两个模型（对标文档第 14 页）
# ==========================================
print("正在加载模型...")
text_model = SentenceTransformer("BAAI/bge-small-zh-v1.5", device="cpu")  # 文本：512
clip = SentenceTransformer("clip-ViT-B-32")  # 图像：512

# 连接你的 ES (替代文档里的 Milvus)，凭据从环境变量/.env 读取
es = Elasticsearch(
    ["https://localhost:9200"],
    basic_auth=("elastic", os.getenv("ES_PASSWORD", "")),
    ca_certs=os.getenv("ES_CA_CERTS", "")
)

# 你的 DeepSeek API (替代文档里的 ollama chat)
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY", "")
DEEPSEEK_URL = "https://api.deepseek.com/chat/completions"


# ==========================================
# 2. 通用向量检索函数（完全对标文档第 14 页）
# ==========================================
def vector_search(model, col_name, query, top_k, is_text):
    """
    向量检索
    :param model: 模型 (text_model 或 clip)
    :param col_name: 集合名称 (文档里是 Milvus 的集合，这里对应 ES 的字段)
    :param query: 问题
    :param top_k: 检索的 top_k
    :param is_text: 是否是文本
    """
    # 1. 查询问题向量化
    if is_text:
        vec = model.encode([query], normalize_embeddings=True)[0].tolist()
        target_field = "bge_vector"  # ES 里的文本向量字段
    else:
        vec = model.encode(query, normalize_embeddings=True).tolist()  # 图片路径 -> 向量
        target_field = "clip_vector"  # ES 里的图片向量字段

    # 2. 检索 (对标 Milvus 的 col.search)
    resp = es.search(index="multimodal_docs", body={
        "knn": {
            "field": target_field,
            "query_vector": vec,
            "k": top_k,
            "num_candidates": top_k * 10
        }
    })

    # 3. 返回 text 字段 (如果是图片，text 存的就是图片的 caption / OCR 内容)
    return [hit["_source"]["content"] for hit in resp["hits"]["hits"]]


# ==========================================
# 3. 图文混合问答主函数（完全对标文档第 15 页）
# ==========================================
def answer(question: str, image_path: str = None) -> str:
    """图文混合问答: 文字+可选图片"""
    retrieved = []

    # 1. 文字检索: 文本问题 -> 文本向量检索 (BGE)
    retrieved += vector_search(text_model, "multimodal_docs", question, top_k=3, is_text=True)

    # 2. 如果用户上传了图片，用 CLIP 做图文检索（图查图）
    if image_path:
        retrieved += vector_search(clip, "multimodal_docs", image_path, top_k=2, is_text=False)

    # 3. 拼接检索到的内容，交给 LLM 生成答案
    context = "\n".join(f"- {r}" for r in retrieved)

    # 拼接 Prompt，调 DeepSeek
    prompt = f"基于给定上下文回答问题，上下文没有就说明不知道。\n\n上下文:\n{context}\n\n问题: {question}"

    headers = {"Authorization": f"Bearer {DEEPSEEK_API_KEY}", "Content-Type": "application/json"}
    data = {
        "model": "deepseek-chat",
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.1
    }

    try:
        resp = requests.post(DEEPSEEK_URL, headers=headers, json=data)
        return resp.json()['choices'][0]['message']['content']
    except Exception as e:
        return f"大模型生成失败: {e}"


# ==========================================
# 4. 测试接口
# ==========================================
@app.get("/ask")
def ask_api(question: str, image_path: str = None):
    """浏览器直接访问这个接口测试"""
    ans = answer(question, image_path)
    return {"answer": ans}


if __name__ == "__main__":
    import uvicorn

    # 在终端里直接测试文档里的两个问题
    print("Q: 公司的入职流程是什么？")
    print("A:", answer("公司的入职流程是什么？"))
    print("\nQ: 这张合同的有效期到什么时候？（附带图片）")
    # 找一个你 data/images 下的图片测试
    print("A:", answer("这张图片里写了什么？", image_path="data/images/img1_card.png"))

    print("\n启动 FastAPI 服务...")
    uvicorn.run(app, host="0.0.0.0", port=8001)