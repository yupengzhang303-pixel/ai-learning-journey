from langchain_text_splitters import RecursiveCharacterTextSplitter


def build_pipeline(text: str):
    """
    文档
        |
        |
    parent大切片
        |
        |
    child小切片

    parent:
        给LLM提供完整上下文

    child:
        给向量数据库检索
    """


    # =====================
    # 第一层：父级切片（大块，供 LLM 获取完整上下文）
    # =====================

    parent_splitter = RecursiveCharacterTextSplitter(

        chunk_size=500,

        chunk_overlap=50,

        separators=[
            "\n\n",
            "\n",
            "。",
            "，",
            ",",
            " "
        ]
    )


    parents = parent_splitter.split_text(text)



    # =====================
    # 第二层：子级切片（小块，用于向量检索，命中后返回其父块）
    # 注意：child 必须小于 parent，否则子块无法映射到单个父块
    # =====================

    child_splitter = RecursiveCharacterTextSplitter(

        chunk_size=300,

        chunk_overlap=50,

        separators=[
            "\n\n",
            "\n",
            "。",
            "，",
            ",",
            " "
        ]

    )


    children = []


    # child必须从parent里面切

    for parent_id, parent in enumerate(parents):

        child_chunks = child_splitter.split_text(parent)


        for child in child_chunks:

            children.append(
                {
                    "parent_id": parent_id,
                    "parent": parent,
                    "child": child
                }
            )


    return parents, children