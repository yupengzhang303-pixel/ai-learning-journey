package com.saas.kb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.kb.common.BizException;
import com.saas.kb.common.ResultCode;
import com.saas.kb.entity.KnowledgeBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KnowledgeBaseServiceTest {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    private static final Long TENANT_1 = 1L;
    private static final Long TENANT_2 = 2L;

    @Test
    void testPage_Normal() {
        IPage<KnowledgeBase> page = knowledgeBaseService.page(TENANT_1, null, 1, 10);
        assertNotNull(page);
        assertTrue(page.getSize() >= 0);
    }

    @Test
    void testPage_WithNameSearch() {
        IPage<KnowledgeBase> page = knowledgeBaseService.page(TENANT_1, "课堂", 1, 10);
        assertNotNull(page);
    }

    @Test
    void testPage_TenantIsolation() {
        IPage<KnowledgeBase> page1 = knowledgeBaseService.page(TENANT_1, null, 1, 10);
        IPage<KnowledgeBase> page2 = knowledgeBaseService.page(TENANT_2, null, 1, 10);
        assertNotNull(page1);
        assertNotNull(page2);
        // 不同租户数据互不可见
        if (page1.getTotal() > 0) {
            assertTrue(page2.getRecords().stream()
                    .noneMatch(kb -> page1.getRecords().stream()
                            .anyMatch(kb1 -> kb1.getId().equals(kb.getId()))));
        }
    }

    @Test
    void testCreate_Success() {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName("测试知识库");
        kb.setDescription("测试描述");
        KnowledgeBase created = knowledgeBaseService.create(kb, TENANT_1);
        assertNotNull(created.getId());
        assertEquals(TENANT_1, created.getTenantId());
        assertEquals("测试知识库", created.getName());
        assertNotNull(created.getCreateTime());
        assertNotNull(created.getUpdateTime());
    }

    @Test
    void testGetById_NotFound() {
        BizException ex = assertThrows(BizException.class, () -> {
            knowledgeBaseService.getById(999999L, TENANT_1);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testGetById_TenantMismatch() {
        // 先创建一个知识库
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName("租户1的知识库");
        KnowledgeBase created = knowledgeBaseService.create(kb, TENANT_1);
        // 用租户2去访问 → 应抛越权异常
        BizException ex = assertThrows(BizException.class, () -> {
            knowledgeBaseService.getById(created.getId(), TENANT_2);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testDelete_TenantMismatch() {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName("租户1的知识库");
        KnowledgeBase created = knowledgeBaseService.create(kb, TENANT_1);
        // 用租户2删除 → 应抛越权异常
        BizException ex = assertThrows(BizException.class, () -> {
            knowledgeBaseService.delete(created.getId(), TENANT_2);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testDelete_Success() {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName("待删除知识库");
        KnowledgeBase created = knowledgeBaseService.create(kb, TENANT_1);
        assertDoesNotThrow(() -> knowledgeBaseService.delete(created.getId(), TENANT_1));
    }

    @Test
    void testCountDocuments() {
        Long count = knowledgeBaseService.countDocuments(2097509583375892482L, TENANT_1);
        assertNotNull(count);
        assertTrue(count >= 0);
    }
}
