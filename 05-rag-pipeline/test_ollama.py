
import os
from ollama import Client

print("=== Python 代理环境变量 ===")
for key, value in os.environ.items():
    if "proxy" in key.lower():
        print(f"{key}={value}")

print("\n=== 测试 1：默认客户端 ===")
try:
    client = Client(host="http://127.0.0.1:11434")
    result = client.chat(
        model="qwen2.5:7b",
        messages=[{"role": "user", "content": "你好"}],
        stream=False,
    )
    print("成功：", result.message.content)
except Exception as e:
    print("失败：", repr(e))

print("\n=== 测试 2：禁用环境代理 ===")
try:
    client = Client(
        host="http://127.0.0.1:11434",
        trust_env=False,
    )
    result = client.chat(
        model="qwen2.5:7b",
        messages=[{"role": "user", "content": "你好"}],
        stream=False,
    )
    print("成功：", result.message.content)
except Exception as e:
    print("失败：", repr(e))
