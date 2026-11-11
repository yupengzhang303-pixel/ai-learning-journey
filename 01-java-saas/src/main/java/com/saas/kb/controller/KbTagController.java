package com.saas.kb.controller;

import com.saas.kb.common.Result;
import com.saas.kb.entity.Document;
import com.saas.kb.entity.KbTag;
import com.saas.kb.service.KbTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/api/v1/tags")
public class KbTagController {

    @Autowired
    private KbTagService tagService;

    @Operation(summary = "查询租户下所有标签")
    @GetMapping
    public Result<List<KbTag>> listByTenant(@RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(tagService.listByTenant(tenantId));
    }

    @Operation(summary = "新建标签")
    @PostMapping
    public Result<KbTag> create(@Valid @RequestBody KbTag tag,
                              @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(tagService.create(tag, tenantId));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader("X-Tenant-Id") Long tenantId) {
        tagService.delete(id, tenantId);
        return Result.ok();
    }

    @Operation(summary = "给文档打标签")
    @PostMapping("/documents/{documentId}/tags")
    public Result<Void> bindTags(@PathVariable Long documentId,
                                 @RequestBody List<Long> tagIds,
                                 @RequestHeader("X-Tenant-Id") Long tenantId) {
        tagService.bindTags(documentId, tagIds, tenantId);
        return Result.ok();
    }

    @Operation(summary = "按标签查文档列表")
    @GetMapping("/{id}/documents")
    public Result<List<Document>> listByTag(@PathVariable Long id,
                                            @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(tagService.listByTag(id, tenantId));
    }
}
