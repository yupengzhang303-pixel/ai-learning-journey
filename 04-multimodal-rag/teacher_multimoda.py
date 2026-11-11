import os
from pymilvus import connections, Collection, CollectionSchema, FieldSchema, DataType, utility
from sentence_transformers import SentenceTransformer

# ==========================================
# 1. 连接本地 Milvus 服务 (相当于启动数据库)
# ==========================================
# 如果你的电脑没装过 milvus-server，这段代码会自动在本地拉取并启动一个轻量级服务
try:
    from milvus import default_server

    default_server.set_base_dir("milvus_data")
    default_server.start()
    connections.connect(host="127.0.0.1", port="19530")
    print("✅ 本地 Milvus 启动并连接成功")
except Exception as e:
    print(f"Milvus 启动失败: {e}")
    exit(1)

# ==========================================
# 2. 加载双模型 (按照老师要求：文本 BGE，图片 CLIP)
# ==========================================
print("正在加载 BGE 和 CLIP 模型...")
text_model = SentenceTransformer("BAAI/bge-small-zh-v1.5", device="cpu")
clip_model = SentenceTransformer("clip-ViT-B-32", device="cpu")


# 注意：BGE 输出 512维，CLIP 也输出 512维，但这俩是不互通的独立空间！

# ==========================================
# 3. 创建两个集合 (文本集合 + 图片集合)
# ==========================================
def ensure_collection(name: str, dim: int):
    if utility.has_collection(name):
        utility.drop_collection(name)

    fields = [
        FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
        FieldSchema(name="text", dtype=DataType.VARCHAR, max_length=2000),  # 图片集合里存 Caption
        FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=dim)
    ]
    col = Collection(name, schema=CollectionSchema(fields=fields, description=name))
    col.create_index(
        field_name="embedding",
        index_params={"index_type": "HNSW", "metric_type": "IP", "params": {"M": 16, "efConstruction": 200}}
    )
    print(f"✅ 集合 {name} 创建成功")
    return col


text_col = ensure_collection("text_collection", dim=512)
img_col = ensure_collection("image_collection", dim=512)

# ==========================================
# 4. 数据入库
# ==========================================
# A. 文本入库 (用 BGE)
texts = [
    "星河科技有限公司成立于2020年，总部位于杭州，致力于人工智能。",
    "员工带薪年假：入职满1年享有5天，满10年享有10天。",
    "差旅报销规定：出差结束后30天内提交报销单。",
    "公司网络安全管理：密码需每90天更换一次。",
    "RAG技术原理：检索增强生成，减少大模型的幻觉问题。"
]
text_embeddings = text_model.encode(texts, normalize_embeddings=True).tolist()
text_col.insert([texts, text_embeddings])
text_col.flush()
print(f"📝 已写入 {text_col.num_entities} 条文本")

# B. 图片入库 (用 CLIP 处理图片本身，用文本存 Caption)
images = [
    {"path": "data/images/img1_card.png", "caption": "员工工牌：李文杰，技术部，工号E001"},
    {"path": "data/images/img2_chart.png", "caption": "2026年Q3销售额柱状图，总计1500万"},
    {"path": "data/images/img3_logo.png", "caption": "星河科技公司的官方Logo"}
]
captions = [img["caption"] for img in images]
img_paths = [img["path"] for img in images]

# 核心：CLIP 直接把图片文件变向量
image_embeddings = clip_model.encode(img_paths, normalize_embeddings=True).tolist()
img_col.insert([captions, image_embeddings])
img_col.flush()
print(f"🖼️ 已写入 {img_col.num_entities} 张图片")

# ==========================================
# 5. 验证跨模态检索 (见证奇迹)
# ==========================================
img_col.load()
text_col.load()

print("\n--- 🔍 跨模态测试：用文字搜图片 (CLIP) ---")
query_text = "一张公司的商标logo"
# 用 CLIP 把文字变成向量
query_vec = clip_model.encode([query_text], normalize_embeddings=True).tolist()

results = img_col.search(
    data=query_vec,
    anns_field="embedding",
    param={"metric_type": "IP", "params": {"ef": 64}},
    limit=1,
    output_fields=["text"]
)
for hit in results[0]:
    print(f"查询文字: {query_text}")
    print(f"找到图片 (Caption): {hit.entity.get('text')}, 相似度得分: {hit.distance:.4f}")