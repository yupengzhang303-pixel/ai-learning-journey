package com.saas.kb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.saas.kb.common.BizException;
import com.saas.kb.common.ResultCode;
import com.saas.kb.entity.Document;
import com.saas.kb.entity.KnowledgeBase;
import com.saas.kb.mapper.DocumentMapper;
import com.saas.kb.mapper.KnowledgeBaseMapper;
import com.saas.kb.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public IPage<Document> page(Long tenantId, Long kbId, String title, Integer status, int pageNum, int pageSize) {
        Page<Document> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getTenantId, tenantId);
        if (kbId != null) {
            wrapper.eq(Document::getKbId, kbId);
        }
        if (title != null && !title.isBlank()) {
            wrapper.like(Document::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Document::getStatus, status);
        }
        wrapper.orderByDesc(Document::getCreateTime);
        return documentMapper.selectPage(page, wrapper);
    }

    @Override
    public Document getById(Long id, Long tenantId) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getId, id).eq(Document::getTenantId, tenantId);
        Document doc = documentMapper.selectOne(wrapper);
        if (doc == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        return doc;
    }

    @Override
    public Document create(Document doc, Long tenantId) {
        doc.setTenantId(tenantId);
        documentMapper.insert(doc);
        return doc;
    }

    @Override
    public Document update(Document doc, Long tenantId) {
        // 越权校验
        getById(doc.getId(), tenantId);
        documentMapper.updateById(doc);
        return getById(doc.getId(), tenantId);
    }

    @Override
    public void delete(Long id, Long tenantId) {
        // 越权校验
        getById(id, tenantId);
        documentMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids, Long tenantId) {
        for (Long id : ids) {
            delete(id, tenantId);
        }
    }

    @Override
    public void move(Long documentId, Long targetKbId, Long tenantId) {
        // 越权校验：确保文档归属当前租户
        Document doc = getById(documentId, tenantId);
        // 越权校验：确保目标知识库归属当前租户
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getId, targetKbId).eq(KnowledgeBase::getTenantId, tenantId);
        KnowledgeBase kb = knowledgeBaseMapper.selectOne(wrapper);
        if (kb == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        // 更新文档所属知识库
        LambdaUpdateWrapper<Document> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Document::getId, documentId).eq(Document::getTenantId, tenantId);
        updateWrapper.set(Document::getKbId, targetKbId);
        documentMapper.update(null, updateWrapper);
    }

    @Override
    public Long countByTenant(Long tenantId) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getTenantId, tenantId);
        return documentMapper.selectCount(wrapper);
    }
}
