package com.saas.kb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saas.kb.common.BizException;
import com.saas.kb.common.ResultCode;
import com.saas.kb.entity.Document;
import com.saas.kb.entity.DocumentTag;
import com.saas.kb.entity.KbTag;
import com.saas.kb.mapper.DocumentMapper;
import com.saas.kb.mapper.DocumentTagMapper;
import com.saas.kb.mapper.KbTagMapper;
import com.saas.kb.service.KbTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KbTagServiceImpl implements KbTagService {

    @Autowired
    private KbTagMapper tagMapper;

    @Autowired
    private DocumentTagMapper documentTagMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public List<KbTag> listByTenant(Long tenantId) {
        LambdaQueryWrapper<KbTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbTag::getTenantId, tenantId).orderByDesc(KbTag::getCreateTime);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public KbTag create(KbTag tag, Long tenantId) {
        tag.setTenantId(tenantId);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public void delete(Long id, Long tenantId) {
        getById(id, tenantId);
        tagMapper.deleteById(id);
    }

    @Override
    public void bindTags(Long documentId, List<Long> tagIds, Long tenantId) {
        LambdaQueryWrapper<DocumentTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentTag::getDocumentId, documentId);
        documentTagMapper.delete(wrapper);
        for (Long tagId : tagIds) {
            DocumentTag dt = new DocumentTag();
            dt.setDocumentId(documentId);
            dt.setTagId(tagId);
            documentTagMapper.insert(dt);
        }
    }

    @Override
    public List<Document> listByTag(Long tagId, Long tenantId) {
        getById(tagId, tenantId);
        LambdaQueryWrapper<DocumentTag> dtWrapper = new LambdaQueryWrapper<>();
        dtWrapper.eq(DocumentTag::getTagId, tagId);
        List<DocumentTag> dtList = documentTagMapper.selectList(dtWrapper);
        if (dtList.isEmpty()) {
            return List.of();
        }
        List<Long> docIds = dtList.stream().map(DocumentTag::getDocumentId).toList();
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Document::getId, docIds).eq(Document::getTenantId, tenantId);
        return documentMapper.selectList(wrapper);
    }

    private KbTag getById(Long id, Long tenantId) {
        LambdaQueryWrapper<KbTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbTag::getId, id).eq(KbTag::getTenantId, tenantId);
        KbTag tag = tagMapper.selectOne(wrapper);
        if (tag == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        return tag;
    }
}
