import os
import json
import easyocr
from dotenv import load_dotenv
from sentence_transformers import SentenceTransformer
from elasticsearch import Elasticsearch
from langchain_text_splitters import RecursiveCharacterTextSplitter  # 新增切片工具

load_dotenv()  # 从项目根目录 .env 读取密钥（参考 .env.example）

print("正在加载 BGE 和 CLIP 模型，请稍候...")
bge_model = SentenceTransformer("BAAI/bge-small-zh-v1.5")
clip_model = SentenceTransformer("clip-ViT-B-32")
reader = easyocr.Reader(['ch_sim', 'en'], gpu=False)

# 凭据从环境变量/.env 读取
es = Elasticsearch(
    ["https://localhost:9200"],
    basic_auth=("elastic", os.getenv("ES_PASSWORD", "")),
    ca_certs=os.getenv("ES_CA_CERTS", "")
)


def recreate_index():
    index_name = "multimodal_docs"
    if es.indices.exists(index=index_name):
        es.indices.delete(index=index_name)

    mapping = {
        "mappings": {
            "properties": {
                "content": {"type": "text"},
                "bge_vector": {"type": "dense_vector", "dims": 512, "index": True, "similarity": "cosine"},
                "clip_vector": {"type": "dense_vector", "dims": 512, "index": True, "similarity": "cosine"},
                "doc_type": {"type": "keyword"},
                "image_path": {"type": "keyword"}
            }
        }
    }
    es.indices.create(index=index_name, body=mapping)
    print(f"✅ 索引 {index_name} 重建成功")


def process_and_insert():
    recreate_index()
    docs_to_insert = []

    # 1. 智能切片器：按 300 字切分，重叠 50 字
    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=300,
        chunk_overlap=50,
        separators=["\n\n", "\n", "。", "！", "？", " ", ""]
    )

    # --- 处理所有文本 ---
    text_dir = "data/texts"
    if os.path.exists(text_dir):
        for filename in os.listdir(text_dir):
            if filename.endswith(".txt"):
                with open(os.path.join(text_dir, filename), "r", encoding="utf-8") as f:
                    content = f.read().strip()

                # 核心升级：如果文本太长，自动切分成多个 chunk
                chunks = text_splitter.split_text(content)
                for i, chunk in enumerate(chunks):
                    bge_vec = bge_model.encode(chunk, normalize_embeddings=True).tolist()
                    docs_to_insert.append({
                        "content": chunk,
                        "bge_vector": bge_vec,
                        "clip_vector": None,
                        "doc_type": "text",
                        "image_path": ""
                    })
                print(f"  已处理文本: {filename} (切分为 {len(chunks)} 个片段)")

    # --- 处理所有图片 ---
    img_dir = "data/images"
    caption_file = os.path.join(img_dir, "captions.json")

    if os.path.exists(caption_file):
        with open(caption_file, "r", encoding="utf-8") as f:
            captions = json.load(f)

        for img_name, caption in captions.items():
            img_path = os.path.join(img_dir, img_name)
            if not os.path.exists(img_path):
                print(f"  ⚠️ 找不到图片: {img_name}，跳过")
                continue

            try:
                ocr_text = "\n".join(reader.readtext(img_path, detail=0))
            except:
                ocr_text = ""

            combined_text = f"【图片描述】：{caption}\n【图片OCR内容】：{ocr_text}"
            bge_vec = bge_model.encode(combined_text, normalize_embeddings=True).tolist()
            clip_vec = clip_model.encode(img_path, normalize_embeddings=True).tolist()

            docs_to_insert.append({
                "content": combined_text,
                "bge_vector": bge_vec,
                "clip_vector": clip_vec,
                "doc_type": "image",
                "image_path": img_path
            })
            print(f"  已处理图片: {img_name}")

    # 写入 ES
    if docs_to_insert:
        for doc in docs_to_insert:
            es.index(index="multimodal_docs", document=doc)
        es.indices.refresh(index="multimodal_docs")
        print(f"\n🎉 多模态双向量入库成功！共计 {len(docs_to_insert)} 条数据。")
    else:
        print("⚠️ 没有找到任何可入库的数据。")


if __name__ == '__main__':
    process_and_insert()