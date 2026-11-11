package com.saas.kb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.kb.entity.KnowledgeBase;

public interface KnowledgeBaseService {

    /**
     * 分页查询知识库
     */
    IPage<KnowledgeBase> page(Long tenantId, String name, int pageNum, int pageSize);

    /**
     * 按ID查询单个知识库（越权校验）
     */
    KnowledgeBase getById(Long id, Long tenantId);

    /**
     * 新建知识库
     */
    KnowledgeBase create(KnowledgeBase kb, Long tenantId);

    /**
     * 修改知识库（越权校验）
     */
    KnowledgeBase update(KnowledgeBase kb, Long tenantId);

    /**
     * 删除知识库（越权校验）
     */
    void delete(Long id, Long tenantId);

    /**
     * 统计知识库下的文档数量
     */
    Long countDocuments(Long kbId, Long tenantId);
}
