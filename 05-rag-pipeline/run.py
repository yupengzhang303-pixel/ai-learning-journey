from pathlib import Path

from splitter_demo import build_pipeline


BASE_DIR = Path(__file__).resolve().parent


file_path = BASE_DIR / "knowledge.txt"


with open(
    file_path,
    "r",
    encoding="utf-8"
) as f:
    text = f.read()



parents, children = build_pipeline(text)



print("=======================")

print(f"原文长度：{len(text)}")

print(f"父切片数量：{len(parents)}")

print(f"子切片数量：{len(children)}")


print("=======================")


print("第一个父切片：")

print(parents[0])


print("=======================")


print("第一个子切片：")

print(children[0]["child"])


print("=======================")


print("对应父切片：")

print(children[0]["parent"])