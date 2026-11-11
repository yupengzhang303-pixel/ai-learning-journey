from pypdf import PdfReader
from docx import Document
import pandas as pd


def parse_pdf(path: str) -> str:
    """
    PDF解析
    """

    reader = PdfReader(path)

    text = []

    for page in reader.pages:
        content = page.extract_text()

        if content:
            text.append(content)

    return "\n".join(text)



def parse_words(path: str) -> str:
    """
    Word解析
    """

    doc = Document(path)

    text = []

    for p in doc.paragraphs:

        if p.text.strip():

            text.append(p.text)


    return "\n".join(text)



def parse_excel(path: str) -> str:
    """
    Excel解析
    """

    df = pd.read_excel(path)

    return df.to_markdown(
        index=False
    )



def parse(path: str) -> str:
    """
    统一入口
    """

    file = path.lower()


    if file.endswith(".pdf"):

        return parse_pdf(path)


    elif file.endswith(".docx"):

        return parse_words(path)


    elif file.endswith(".xlsx"):

        return parse_excel(path)


    else:

        raise Exception(
            "暂不支持该文件格式"
        )



if __name__ == "__main__":


    file_path = "knowledge.pdf"


    text = parse(file_path)


    print("================")

    print(text[:1000])