from ollama import chat


# Plan-Execute 架构
def plan_execute(task: str) -> str:
    # ==================== 阶段1：规划（Plan） ====================
    # 加强了 Prompt 约束，禁止它生成太多的步骤
    plan_prompt = f"""
    请把下面的任务拆解成3~5个可执行的步骤，每行一个步骤（不要编号，直接写出步骤内容）。
    【注意：必须严格控制在3到5个步骤，严禁超过5步！】
    任务：{task}
    步骤：
    """
    plan_resp = chat(
        model="qwen2.5:7b",
        messages=[
            {
                "role": "user",
                "content": plan_prompt
            }
        ]
    )
    plan = plan_resp['message']['content'].strip()
    print("【计划】\n" + plan)

    # 把计划按行拆分成步骤列表
    steps = [s.strip() for s in plan.split("\n") if s.strip()]
    # 【核心修复】：强制只取前 5 步，防止模型失控生成太多导致程序跑不完
    steps = steps[:5]

    # ==================== 阶段2：执行（Execute） ====================
    results = []  # 存放每个步骤执行的结果
    for i, step in enumerate(steps):
        exec_resp = chat(
            model="qwen2.5:7b",
            messages=[
                {
                    "role": "system",
                    "content": "你是执行者，按步骤执行并给出该步骤的结果"
                },
                {
                    "role": "user",
                    "content": f"任务：{task}\n当前执行第{i + 1}步：{step}"
                }
            ]
        )
        # 将每一步的结果保存下来
        results.append(f"步骤{i + 1}：{exec_resp['message']['content']}")
        print(f"【执行步骤 {i + 1}】已完成: {step}")

    # ==================== 阶段3：汇总（Summarize） ====================
    print("\n正在汇总最终结果，请稍候...")
    summary = chat(
        model="qwen2.5:7b",
        messages=[
            {
                "role": "user",
                "content": "请把以下执行结果汇总成最终答案：\n" + "\n".join(results)
            }
        ]
    )
    return summary['message']['content']


if __name__ == '__main__':
    answer = plan_execute("写一份杭州两日游攻略")
    print(f"\n================ 【最终答案】 ================\n")
    print(answer)