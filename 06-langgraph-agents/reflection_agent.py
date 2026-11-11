from ollama import chat


def reflect_and_improve(task: str, max_rounds: int = 3) -> str:
    answer = ""

    for r in range(max_rounds):
        print(f"\n================ 第 {r + 1} 轮 ================")

        # ==================== 1. 生成答案（作者角色） ====================
        resp = chat(
            model="qwen2.5:7b",
            messages=[
                {
                    "role": "user",
                    "content": f"完成任务：{task}"
                }
            ]
        )
        answer = resp['message']['content']
        print(f"【当前生成答案】\n{answer}")

        # ==================== 2. 反思（评审者角色） ====================
        critic = chat(
            model="qwen2.5:7b",
            messages=[
                {
                    "role": "system",
                    "content": "你是一位严格的评审，找出答案的不足。如果完美无缺，请严格只回复两个字母：OK"
                },
                {
                    "role": "user",
                    "content": f"任务：{task}\n答案是：{answer}\n请指出不足（如果没有，回复OK）："
                }
            ]
        )
        feedback = critic['message']['content']
        print(f"\n【第 {r + 1} 轮评审反馈】\n{feedback}")

        # ==================== 3. 判断与改进 ====================
        # 如果评审说 OK，直接结束循环
        if "OK" in feedback:
            print("\n✅ 评审通过，结束迭代！")
            break

        # 如果评审指出了不足，把反馈拼回任务里，让下一轮的作者带着问题去改
        task = f"{task}\n\n(上一版的不足及改进建议：{feedback})"

    return answer


if __name__ == '__main__':
    print("【最终答案】\n" + reflect_and_improve("写一首关于秋天的五言绝句", 3))