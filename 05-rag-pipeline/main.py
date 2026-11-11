from fastapi import FastAPI
from pydantic import BaseModel
import uvicorn

# 1. 创建 Web 应用
app = FastAPI(title="文档解析服务")

# 2. 定义请求体（类似 Java 的 DTO + 校验注解）
class ParseReq(BaseModel):
    content: str                          # 必填字段
    fileName: str = "default.txt"         # 可选字段，有默认值

# 3. POST 接口：解析文档
@app.post("/parse")
def parse(req: ParseReq):
    chars = len(req.content)
    return {
        "chars": chars,
        "fileName": req.fileName,
        "preview": req.content[:50]      # 前 50 个字符
    }

# 4. GET 接口：健康检查
@app.get("/health")
def health():
    return {"status": "ok"}

# 5. 启动服务
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)