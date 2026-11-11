from typing import TypedDict
from ollama import chat
from langgraph.graph import StateGraph, START, END

# 1. 基础调用封装
def ask(prompt: str) -> str:
    return chat(
        model="qwen2.5:7b",
        messages=[{"role": "user", "content": prompt}]
    )['message']['content']

# 2. 定义状态
class State(TypedDict):
    content: str    # 舆情内容
    sentiment: str  # 情感：正面/负面/中性
    level: str      # 严重程度：高/中/低

# 3. 节点定义
def sentiment_node(state: State) -> dict:
    """节点1：LLM 情感判断"""
    res = ask(f"判断这条舆情的情绪倾向，只返回一个词【正面/负面/中性】\n{state['content']}").strip()
    return {"sentiment": res}

def grade_node(state: State) -> dict:
    """节点2：LLM 负面分级"""
    res = ask(f"这条舆情被判定为负面，评估其严重程度，只返回一个词【高/中/低】\n{state['content']}").strip()
    return {"level": res}

# 4. 条件边（路由函数）
def route(state: State) -> str:
    """根据情感判断结果决定下一步：负面 -> 去分级；非负面 -> 直接结束"""
    if state['sentiment'] == '负面':
        return 'grade'
    return 'end'

# 5. 构建图
graph = StateGraph(State)
graph.add_node("sentiment", sentiment_node)
graph.add_node("grade", grade_node)

# 连线与条件分支
graph.add_edge(START, "sentiment")
graph.add_conditional_edges("sentiment", route, {"grade": "grade", "end": END})
graph.add_edge("grade", END)

app = graph.compile()

# 6. 测试运行
if __name__ == '__main__':
    for msg in ["这个产品太差劲了，我要投诉", "今天收到货，质量很好，五星好评"]:
        print(f"\n>>> 正在处理消息: {msg}")
        res = app.invoke({"content": msg})
        print(f"最终结果 -> 情感={res['sentiment']}, 级别={res.get('level', '无')}")