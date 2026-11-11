import os

# ==============================
# 环境配置
# ==============================

# Ollama 本地服务不要走代理
os.environ["NO_PROXY"] = "localhost,127.0.0.1"
os.environ["no_proxy"] = "localhost,127.0.0.1"

# HuggingFace 国内镜像
# 如果你使用代理，也可以删除这一行
os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"


from pathlib import Path

from langchain_ollama import ChatOllama
from langchain_community.document_loaders import TextLoader
from langchain_community.vectorstores import FAISS
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings



# ==============================
# 1. 加载知识库
# ==============================

BASE_DIR = Path(__file__).resolve().parent

knowledge_path = BASE_DIR / "knowledge.txt"


if not knowledge_path.exists():
    raise FileNotFoundError(
        f"找不到知识库文件: {knowledge_path}"
    )


loader = TextLoader(
    str(knowledge_path),
    encoding="utf-8"
)

documents = loader.load()


if not documents:
    raise ValueError(
        "知识库为空，请检查 knowledge.txt"
    )


print(
    f"知识库加载成功，共 {len(documents)} 个文档"
)



# ==============================
# 2. 文档切分
# ==============================

splitter = RecursiveCharacterTextSplitter(
    chunk_size=200,
    chunk_overlap=40
)


chunks = splitter.split_documents(
    documents
)


print(
    f"文档切分完成，共 {len(chunks)} 个文本块"
)



# ==============================
# 3. 初始化向量模型
# ==============================

print("正在加载 Embedding 模型...")


embeddings = HuggingFaceEmbeddings(
    model_name="BAAI/bge-small-zh-v1.5",
    model_kwargs={
        "device": "cpu"
    },
    encode_kwargs={
        "normalize_embeddings": True
    }
)


print("Embedding模型加载完成")



# ==============================
# 4. 创建 FAISS 向量库
# ==============================

print("正在创建向量数据库...")


vect_store = FAISS.from_documents(
    chunks,
    embeddings
)


retriever = vect_store.as_retriever(
    search_kwargs={
        "k": 2
    }
)


print("FAISS向量库创建完成")



# ==============================
# 5. 初始化 Ollama
# ==============================

llm = ChatOllama(
    model="qwen2.5:7b",
    base_url="http://127.0.0.1:11434",

    # 控制回答随机性
    temperature=0,

    # 最大输出长度
    num_predict=256,

    # 上下文长度
    num_ctx=2048
)



# ==============================
# 6. RAG问答函数
# ==============================

def ask(query: str):

    print("\n开始检索资料...")


    docs = retriever.invoke(query)


    if not docs:
        return "知识库中没有找到相关信息"


    context = "\n\n".join(
        doc.page_content
        for doc in docs
    )


    print("检索完成")


    prompt = f"""
你是一名旅游规划助手。

你的回答必须严格依据下面的知识库资料。

规则：
1. 不允许编造知识库不存在的信息。
2. 如果资料没有答案，请回答：
   "知识库中没有相关信息"
3. 回答尽量简洁。


知识库资料：
----------------
{context}
----------------


用户问题：
{query}


回答：
"""


    print("正在调用大模型...")


    response = llm.invoke(prompt)


    return response.content



# ==============================
# 7. 测试
# ==============================


if __name__ == "__main__":

    question = (
        "我去成都穷游，住宿大概多少钱？"
    )


    print("\n================")
    print("开始测试RAG")
    print("================")


    # 查看检索结果

    hits = retriever.invoke(
        question
    )


    print("\n===== 检索结果 =====")


    for i, doc in enumerate(
        hits,
        start=1
    ):

        print(
            f"\n--- 第{i}条 ---"
        )

        print(
            doc.page_content
        )



    print("\n===== AI回答 =====")


    answer = ask(
        question
    )


    print(answer)