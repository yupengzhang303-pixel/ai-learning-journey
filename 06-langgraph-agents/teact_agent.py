from datetime import datetime
from ollama import chat  # noqa
import re
import json


# 1. 准备工具，定义工具
def search(query: str) -> str:
    """模拟搜索"""
    return f"关于{query}的搜索结果：杭州今天20℃，晴。"


def get_weather(city: str) -> str:
    """查询城市天气"""
    return f"{city}今天晴，20~25℃，空气质量优。"


def get_time() -> str:
    """查询当前时间"""
    return datetime.now().strftime("%y-%m-%d %H:%M:%S")


# 【任务2扩展】新增 get_date 工具
def get_date() -> str:
    """查询今天的日期"""
    return datetime.now().strftime("%Y-%m-%d")


# 工具注册表
TOOLS = {
    "search": (search, "search(query): 搜索网络获取信息"),
    "get_weather": (get_weather, "get_weather(city): 查询指定城市天气"),
    "get_time": (get_time, "get_time(): 返回当前真实的日期和时间，回答（现在几点）前必须先调用它，禁止自己胡编乱造"),
    "get_date": (get_date, "get_date(): 查询今天的日期，回答（今天几号，今天日期）前必须先调用它，禁止自己胡编乱造")
}

# 2. 系统Prompt
STRING_PROMPT = """
你是一个调用工具的AI Agent,可用工具：
{tool_list}
请严格按照以下格式回复（每次只能一个Action）
Thought: 你的思考
Action: 工具名
Action_Input: {{"参数名":"值"}}
当你能回答问题时：
Thought: 我现在可以回答了
Final Answer: 最终答案
【硬性规则】
1. 天气、时间、搜索等实时信息，必须先调用对应工具拿到Observation再回答；禁止凭空编造数字。
2. 一条回复里只能写一个Action；只有拿到回答所需的全部工具结果后，才能输出Final Answer，否则必须继续调用工具，不要提前写FinalAnswer。
"""


# 3. 实现主体循环（完美修复上下文版）
def react_agent(question: str, max_steps: int = 5) -> str:
    tool_list = "\n".join(TOOLS[t][1] for t in TOOLS)
    messages = [
        {"role": "system", "content": STRING_PROMPT.format(tool_list=tool_list)},
        {"role": "user", "content": question}
    ]

    for step in range(max_steps):
        resp = chat(model="qwen2.5:7b", messages=messages)
        text = resp['message']['content']
        print(f"====step{step + 1}===========\n{text}")

        # 把模型输出存入历史
        messages.append({"role": "assistant", "content": text})

        # 检查是否输出 Final Answer
        if "Final Answer" in text:
            json_match = re.search(r'"Final Answer"\s*:\s*"([^"]+)"', text)
            if json_match:
                return json_match.group(1).strip()
            split_text = text.split("Final Answer:")
            if len(split_text) > 1:
                return split_text[-1].strip()
            return text.split("Action_Input:")[-1].replace("{", "").replace("}", "").strip()

        # 提取 Action
        action = re.search(r"Action:\s*(\w+)", text)

        if action:
            tool_name = action.group(1)
            arg_match = re.search(r"Action_Input:\s*(\{[^}]*\})", text)

            try:
                raw = arg_match.group(1) if arg_match else "{}"
                raw = (raw.replace("：", ":").replace("'", '"').replace("“", '"').replace("”", '"'))
                args = json.loads(raw)
            except (json.JSONDecodeError, AttributeError):
                args = {}

            if tool_name in TOOLS:
                try:
                    result = TOOLS[tool_name][0](**args)
                    observation = f"Observation: {result}"
                except TypeError as e:
                    observation = f"Observation: 调用{tool_name}失败：{e}"
                print(observation)
                messages.append({"role": "user", "content": observation})
            else:
                # 【任务3的兜底逻辑】如果工具不存在，喂给模型错误提示
                messages.append({"role": "user",
                                 "content": f"Observation: 工具 {tool_name} 不存在，请尝试用其他方式或直接输出 Final Answer"})
        else:
            messages.append({"role": "user", "content": "请按格式输出 Thought、Action 或 Final Answer"})

    return "达到最大步数，未能完成任务"


# ==================== 1.4 学员实操任务测试 ====================
if __name__ == '__main__':
    print("--- 任务1：观察基础循环 ---")
    react_agent("帮我看一下今天杭州的天气", 5)

    print("\n\n--- 任务2：测试新工具 get_date ---")
    react_agent("今天几号？", 5)

    print("\n\n--- 任务3：兜底测试（问工具答不了的问题） ---")
    react_agent("今天A股涨了吗？", 5)