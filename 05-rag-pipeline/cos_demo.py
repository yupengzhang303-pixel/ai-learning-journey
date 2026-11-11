import numpy as np

# 1. 定义计算器
def cosine_similarity(v1, v2):
    dot_product = np.dot(v1, v2)
    norm_v1 = np.linalg.norm(v1)
    norm_v2 = np.linalg.norm(v2)
    return dot_product / (norm_v1 * norm_v2)

# 2. 准备数据
doc_A = np.array([1.0, 0.0])
doc_B = np.array([0.0, 1.0])
question_Q = np.array([0.8, 0.6])

# 3. 计算并打印
print(f"Q 与 A 的相似度: {cosine_similarity(question_Q, doc_A)}")
print(f"Q 与 B 的相似度: {cosine_similarity(question_Q, doc_B)}")