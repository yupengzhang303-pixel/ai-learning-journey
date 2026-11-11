import numpy as np
import requests
from sentence_transformers import SentenceTransformer

documents = [
    "公司年假：入职满一年的员工每年可享有5天带薪年假。",
    "公司年假：入职满10年的员工每年享有10天带薪年假。",
    "报销流程：员工出差30天内提交报销单，财务审核后打款。",
    "考勤制度：工作日9：00上班，18：00下班，午休12：00~13：30",
    "请假制度：事假须提前1天申请，病假需要提供医院证明。"
]

# 加载向量模型（第一次运行会自动下载模型，需要耐心等待）
model = SentenceTransformer("BAAI/bge-small-zh-v1.5")

# 将文档库转为向量
doc_vectors = model.encode(documents)
print("文档向量化完成，形状:", doc_vectors.shape)


# 3.余弦相似度
def cosine_similarity(a, b):
    """余弦相似度计算，两个向量的接近程度，越近1越相似"""
    return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))


# 4.检索
def retrieve(query, top_k):
    """检索：将用户的提问变成向量，在文档向量中找最相似的top_k个"""
    q_vec = model.encode(query)
    scores = [cosine_similarity(q_vec, dv) for dv in doc_vectors]
    # np.argsort 返回从小到大的下标，[::-1]倒序（从大到小），取前top_k个
    top_idx = np.argsort(scores)[::-1][:top_k]
    # 返回【文档内容，相似度】列表
    return [(documents[i], scores[i]) for i in top_idx]


# 5.拼接prompt+调用LLM
def ask(query):
    """生成，检索资料+拼接prompt-->让大模型根据资料进行回答"""
    hits = retrieve(query, top_k=2)
    # 修正1：解包元组，只取文档内容
    context = "\n".join(f"-{doc}" for doc, _ in hits)

    # 修正2：优化 Prompt 格式
    prompt = f"""请仅根据下面提供的资料回答问题，资料中没有就回答"不知道"。
资料：
{context}
问题：{query}
回答：
"""
    # 调用本地ollama模型
    response = requests.post(
        "http://localhost:11434/api/generate",
        json={
            "model": "qwen2.5:3b",
            "prompt": prompt,
            "stream": False
        }
    )

    # --- 加这两行，便于排错 ---
    res_json = response.json()
    print("Ollama 原始返回:", res_json)
    # -------------------------

    return res_json.get('response', '【未获取到回答】')


# ==========测试==================
if __name__ == '__main__':
    print("问：我入职2年了，有多少天年假？")
    print("答：", ask("我入职2年了，有多少天年假？"))
    print()
    print("问：报销要多久内提交？")
    print("答：", ask("报销要多久内提交？"))
    print()
    print("问：公司食堂在哪里？【知识库中没有提供的资料，看它会不会胡说】")
    print("答：", ask("公司的食堂在哪里？"))