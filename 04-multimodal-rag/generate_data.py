import os
from PIL import Image, ImageDraw, ImageFont
import pandas as pd
import json

# 确保文件夹存在
os.makedirs("data/texts", exist_ok=True)
os.makedirs("data/images", exist_ok=True)


def create_texts():
    texts = {
        "article1_company.txt": "星河科技有限公司成立于2020年，总部位于杭州，主要致力于人工智能与大数据分析。",
        "article2_leave.txt": "员工带薪年假制度：入职满1年享有5天年假，满10年享有10天，满20年享有15天。",
        "article3_expense.txt": "差旅报销规定：出差结束后请在30天内提交报销单，需附带发票和行程单，由财务部审核。",
        "article4_security.txt": "公司网络安全管理：严禁在办公电脑上安装未授权的第三方软件，密码需每90天更换一次。",
        "article5_rag.txt": "RAG技术原理：检索增强生成，通过结合外部知识库和向量检索，减少大语言模型的幻觉问题。"
    }
    for filename, content in texts.items():
        with open(f"data/texts/{filename}", "w", encoding="utf-8") as f:
            f.write(content)
    print("✅ 5篇文章生成完毕！")


def create_images():
    try:
        font = ImageFont.truetype("C:/Windows/Fonts/simhei.ttf", 24)
    except:
        font = ImageFont.load_default()

    image_infos = [
        {"name": "img1_card.png", "text": "员工工牌：李文杰\n部门：技术部\n工号：E001",
         "caption": "这是一张员工工牌的照片，属于技术部的李文杰，工号E001。"},
        {"name": "img2_chart.png", "text": "2026年Q3销售额\n环比增长：25%\n总计：1500万",
         "caption": "这是一张柱状图表，展示了2026年第三季度销售额环比增长25%，总计1500万。"},
        {"name": "img3_logo.png", "text": "星河科技\nXINGHE TECH",
         "caption": "这是星河科技公司的官方Logo，包含中英文名称。"}
    ]

    for info in image_infos:
        img = Image.new("RGB", (500, 300), "white")
        draw = ImageDraw.Draw(img)
        draw.multiline_text((50, 100), info["text"], fill="black", font=font, spacing=10)
        img.save(f"data/images/{info['name']}")

    captions = {info["name"]: info["caption"] for info in image_infos}
    with open("data/images/captions.json", "w", encoding="utf-8") as f:
        json.dump(captions, f, ensure_ascii=False, indent=4)
    print("✅ 3张图片及 Caption 生成完毕！")


if __name__ == '__main__':
    create_texts()
    create_images()