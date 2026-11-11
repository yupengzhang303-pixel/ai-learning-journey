package com.saas.kb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.kb.entity.Document;

import java.util.List;

public interface DocumentService {

    /**
     * 分页查询文档（支持 kbId/title/status 筛选）
     */
    IPage<Document> page(Long tenantId, Long kbId, String title, Integer status, int pageNum, int pageSize);

    /**
     * 按ID查询单个文档（越权校验）
     */
    Document getById(Long id, Long tenantId);

    /**
     * 新建文档
     */
    Document create(Document doc, Long tenantId);

    /**
     * 修改文档（越权校验）
     */
    Document update(Document doc, Long tenantId);

    /**
     * 删除文档（逻辑删除，越权校验）
     */
    void delete(Long id, Long tenantId);

    /**
     * 批量删除文档（事务保证）
     */
    void batchDelete(List<Long> ids, Long tenantId);

    /**
     * 移动文档到其他知识库（越权校验）
     */
    void move(Long documentId, Long targetKbId, Long tenantId);

    /**
     * 统计租户下的文档总数
     */
    Long countByTenant(Long tenantId);
}
