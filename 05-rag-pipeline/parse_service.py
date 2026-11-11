from fastapi import FastAPI
from pydantic import BaseModel
import uvicorn

app = FastAPI(title="文档解析服务")


# ============ 请求模型 ============

class ParseReq(BaseModel):
    content: str
    chunk_size: int = 500


class StatsReq(BaseModel):
    text: str


class StatsResp(BaseModel):
    chars: int   # 字符数（含空格）
    words: int   # 单词数（按空白切分）
    lines: int   # 行数


# ============ 文档解析接口 ============

@app.post("/parse")
def parse(req: ParseReq):
    text = req.content
    chunks = []
    step = req.chunk_size - 50  # 重叠 50 字符

    for i in range(0, len(text), step):
        chunk = text[i:i + req.chunk_size]
        if chunk:
            chunks.append({"index": len(chunks), "text": chunk})
        if i + req.chunk_size >= len(text):
            break

    return {"chunks": chunks}


# ============ 健康检查接口 ============

@app.get("/health")
def health():
    return {"status": "ok"}


# ============ 文本统计接口 ============

@app.post("/stats", response_model=StatsResp)
def stats(req: StatsReq):
    text = req.text
    chars = len(text)
    words = len(text.split())        # 按空白切分
    lines = len(text.splitlines())   # 按换行切分
    return StatsResp(chars=chars, words=words, lines=lines)


# ============ 启动服务 ============

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)