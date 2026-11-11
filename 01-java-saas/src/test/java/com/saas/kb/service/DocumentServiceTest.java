package com.saas.kb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.kb.common.BizException;
import com.saas.kb.common.ResultCode;
import com.saas.kb.entity.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    private static final Long TENANT_1 = 1L;
    private static final Long TENANT_2 = 2L;

    private Long createTestKb() {
        var kb = new com.saas.kb.entity.KnowledgeBase();
        kb.setName("测试知识库_" + System.currentTimeMillis());
        return knowledgeBaseService.create(kb, TENANT_1).getId();
    }

    @Test
    void testPage_Normal() {
        IPage<Document> page = documentService.page(TENANT_1, null, null, null, 1, 10);
        assertNotNull(page);
    }

    @Test
    void testPage_WithKbIdFilter() {
        Long kbId = createTestKb();
        IPage<Document> page = documentService.page(TENANT_1, kbId, null, null, 1, 10);
        assertNotNull(page);
    }

    @Test
    void testPage_WithTitleSearch() {
        IPage<Document> page = documentService.page(TENANT_1, null, "spring", null, 1, 10);
        assertNotNull(page);
    }

    @Test
    void testPage_TenantIsolation() {
        IPage<Document> page1 = documentService.page(TENANT_1, null, null, null, 1, 10);
        IPage<Document> page2 = documentService.page(TENANT_2, null, null, null, 1, 10);
        if (page1.getTotal() > 0) {
            assertTrue(page2.getRecords().stream()
                    .noneMatch(d -> page1.getRecords().stream()
                            .anyMatch(d1 -> d1.getId().equals(d.getId()))));
        }
    }

    @Test
    void testPage_ExcludeDeleted() {
        IPage<Document> page = documentService.page(TENANT_1, null, null, 1, 1, 10);
        assertNotNull(page);
        page.getRecords().forEach(doc -> assertEquals(1, doc.getStatus()));
    }

    @Test
    void testCreate_Success() {
        Long kbId = createTestKb();
        Document doc = new Document();
        doc.setTitle("测试文档");
        doc.setContent("测试内容");
        doc.setKbId(kbId);
        Document created = documentService.create(doc, TENANT_1);
        assertNotNull(created.getId());
        assertEquals(TENANT_1, created.getTenantId());
        assertNotNull(created.getCreateTime());
    }

    @Test
    void testGetById_NotFound() {
        BizException ex = assertThrows(BizException.class, () -> {
            documentService.getById(999999L, TENANT_1);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testGetById_TenantMismatch() {
        Long kbId = createTestKb();
        Document doc = new Document();
        doc.setTitle("租户1文档");
        doc.setKbId(kbId);
        Document created = documentService.create(doc, TENANT_1);
        BizException ex = assertThrows(BizException.class, () -> {
            documentService.getById(created.getId(), TENANT_2);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testDelete_Logical() {
        Long kbId = createTestKb();
        Document doc = new Document();
        doc.setTitle("待删除文档");
        doc.setKbId(kbId);
        Document created = documentService.create(doc, TENANT_1);
        assertDoesNotThrow(() -> documentService.delete(created.getId(), TENANT_1));
        // 再次查询应抛异常（已逻辑删除）
        BizException ex = assertThrows(BizException.class, () -> {
            documentService.getById(created.getId(), TENANT_1);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testDelete_TenantMismatch() {
        Long kbId = createTestKb();
        Document doc = new Document();
        doc.setTitle("租户1文档");
        doc.setKbId(kbId);
        Document created = documentService.create(doc, TENANT_1);
        BizException ex = assertThrows(BizException.class, () -> {
            documentService.delete(created.getId(), TENANT_2);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testMove_TenantMismatch() {
        Long kbId1 = createTestKb();
        Long kbId2 = createTestKb();
        Document doc = new Document();
        doc.setTitle("待移动文档");
        doc.setKbId(kbId1);
        Document created = documentService.create(doc, TENANT_1);
        // 用租户2移动租户1的文档 → 应抛异常
        BizException ex = assertThrows(BizException.class, () -> {
            documentService.move(created.getId(), kbId2, TENANT_2);
        });
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void testBatchDelete() {
        Long kbId = createTestKb();
        Document doc1 = new Document();
        doc1.setTitle("批量删除1");
        doc1.setKbId(kbId);
        Document d1 = documentService.create(doc1, TENANT_1);
        Document doc2 = new Document();
        doc2.setTitle("批量删除2");
        doc2.setKbId(kbId);
        Document d2 = documentService.create(doc2, TENANT_1);
        List<Long> ids = List.of(d1.getId(), d2.getId());
        assertDoesNotThrow(() -> documentService.batchDelete(ids, TENANT_1));
        // 再次查询应抛异常
        assertThrows(BizException.class, () -> documentService.getById(d1.getId(), TENANT_1));
        assertThrows(BizException.class, () -> documentService.getById(d2.getId(), TENANT_1));
    }

    @Test
    void testCountByTenant() {
        Long count = documentService.countByTenant(TENANT_1);
        assertNotNull(count);
        assertTrue(count >= 0);
    }
}
