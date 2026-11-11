package com.saas.kb.service;

import com.saas.kb.entity.Document;
import com.saas.kb.entity.KbTag;

import java.util.List;

public interface KbTagService {

    /**
     * 查询租户下所有标签
     */
    List<KbTag> listByTenant(Long tenantId);

    /**
     * 新建标签
     */
    KbTag create(KbTag tag, Long tenantId);

    /**
     * 删除标签（越权校验）
     */
    void delete(Long id, Long tenantId);

    /**
     * 给文档打标签
     */
    void bindTags(Long documentId, List<Long> tagIds, Long tenantId);

    /**
     * 按标签查文档列表
     */
    List<Document> listByTag(Long tagId, Long tenantId);
}
