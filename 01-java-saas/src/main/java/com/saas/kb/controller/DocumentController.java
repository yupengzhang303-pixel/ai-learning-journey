package com.saas.kb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.kb.common.Result;
import com.saas.kb.entity.Document;
import com.saas.kb.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "文档管理")
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Operation(summary = "分页查询文档")
    @GetMapping
    public Result<IPage<Document>> page(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @Parameter(description = "知识库ID【筛选】")
            @RequestParam(required = false) Long kbId,
            @Parameter(description = "标题【模糊搜索】")
            @RequestParam(required = false) String title,
            @Parameter(description = "状态【1正常 0删除】")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页面大小，默认10")
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(documentService.page(tenantId, kbId, title, status, pageNum, pageSize));
    }

    @Operation(summary = "按ID查询文档")
    @GetMapping("/{id}")
    public Result<Document> getById(@PathVariable Long id,
                                    @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(documentService.getById(id, tenantId));
    }

    @Operation(summary = "新建文档")
    @PostMapping
    public Result<Document> create(@Valid @RequestBody Document doc,
                                   @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(documentService.create(doc, tenantId));
    }

    @Operation(summary = "修改文档")
    @PutMapping("/{id}")
    public Result<Document> update(@PathVariable Long id,
                                   @Valid @RequestBody Document doc,
                                   @RequestHeader("X-Tenant-Id") Long tenantId) {
        doc.setId(id);
        return Result.ok(documentService.update(doc, tenantId));
    }

    @Operation(summary = "删除文档（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader("X-Tenant-Id") Long tenantId) {
        documentService.delete(id, tenantId);
        return Result.ok();
    }

    @Operation(summary = "批量删除文档")
    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids,
                                    @RequestHeader("X-Tenant-Id") Long tenantId) {
        documentService.batchDelete(ids, tenantId);
        return Result.ok();
    }

    @Operation(summary = "移动文档到其他知识库")
    @PutMapping("/{id}/move")
    public Result<Void> move(@PathVariable Long id,
                             @RequestParam Long targetKbId,
                             @RequestHeader("X-Tenant-Id") Long tenantId) {
        documentService.move(id, targetKbId, tenantId);
        return Result.ok();
    }

    @Operation(summary = "统计租户下的文档总数")
    @GetMapping("/count")
    public Result<Long> countByTenant(@RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(documentService.countByTenant(tenantId));
    }
}
