package com.saas.kb.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文档-标签关联实体（复合主键表，仅用于插入/删除）
 */
@Data
@TableName("kb_document_tag")
public class DocumentTag {
    private Long documentId;
    private Long tagId;
}
