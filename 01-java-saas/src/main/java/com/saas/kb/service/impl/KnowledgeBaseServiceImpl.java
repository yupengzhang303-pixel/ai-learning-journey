package com.saas.kb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.kb.common.BizException;
import com.saas.kb.common.ResultCode;
import com.saas.kb.entity.Document;
import com.saas.kb.entity.KnowledgeBase;
import com.saas.kb.mapper.DocumentMapper;
import com.saas.kb.mapper.KnowledgeBaseMapper;
import com.saas.kb.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public IPage<KnowledgeBase> page(Long tenantId, String name, int pageNum, int pageSize) {
        Page<KnowledgeBase> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getTenantId, tenantId);
        if (name != null && !name.isBlank()) {
            wrapper.and(w -> w.like(KnowledgeBase::getName, name).or().like(KnowledgeBase::getDescription, name));
        }
        wrapper.orderByDesc(KnowledgeBase::getCreateTime);
        return knowledgeBaseMapper.selectPage(page, wrapper);
    }

    @Override
    public KnowledgeBase getById(Long id, Long tenantId) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getId, id).eq(KnowledgeBase::getTenantId, tenantId);
        KnowledgeBase kb = knowledgeBaseMapper.selectOne(wrapper);
        if (kb == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        return kb;
    }

    @Override
    public KnowledgeBase create(KnowledgeBase kb, Long tenantId) {
        kb.setTenantId(tenantId);
        knowledgeBaseMapper.insert(kb);
        return kb;
    }

    @Override
    public KnowledgeBase update(KnowledgeBase kb, Long tenantId) {
        // 越权校验：先查再改
        getById(kb.getId(), tenantId);
        knowledgeBaseMapper.updateById(kb);
        return getById(kb.getId(), tenantId);
    }

    @Override
    public void delete(Long id, Long tenantId) {
        // 越权校验
        getById(id, tenantId);
        knowledgeBaseMapper.deleteById(id);
    }

    @Override
    public Long countDocuments(Long kbId, Long tenantId) {
        // 越权校验：确保知识库归属当前租户
        getById(kbId, tenantId);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getKbId, kbId).eq(Document::getTenantId, tenantId);
        return documentMapper.selectCount(wrapper);
    }
}
