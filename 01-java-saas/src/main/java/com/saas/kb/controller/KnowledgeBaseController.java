package com.saas.kb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.kb.common.Result;
import com.saas.kb.entity.KnowledgeBase;
import com.saas.kb.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "知识库管理")
@RestController
@RequestMapping("/api/v1/kbs")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Operation(summary = "分页查询知识库")
    @GetMapping
    public Result<IPage<KnowledgeBase>> page(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @Parameter(description = "知识库名称【模糊搜索】")
            @RequestParam(required = false) String name,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页面大小，默认10")
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(knowledgeBaseService.page(tenantId, name, pageNum, pageSize));
    }

    @Operation(summary = "按ID查询知识库")
    @GetMapping("/{id}")
    public Result<KnowledgeBase> getById(@PathVariable Long id,
                                         @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(knowledgeBaseService.getById(id, tenantId));
    }

    @Operation(summary = "新建知识库")
    @PostMapping
    public Result<KnowledgeBase> create(@Valid @RequestBody KnowledgeBase kb,
                                        @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(knowledgeBaseService.create(kb, tenantId));
    }

    @Operation(summary = "修改知识库")
    @PutMapping("/{id}")
    public Result<KnowledgeBase> update(@PathVariable Long id,
                                        @RequestBody KnowledgeBase kb,
                                        @RequestHeader("X-Tenant-Id") Long tenantId) {
        kb.setId(id);
        return Result.ok(knowledgeBaseService.update(kb, tenantId));
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader("X-Tenant-Id") Long tenantId) {
        knowledgeBaseService.delete(id, tenantId);
        return Result.ok();
    }

    @Operation(summary = "统计知识库下的文档数量")
    @GetMapping("/{id}/count")
    public Result<Long> countDocuments(@PathVariable Long id,
                                       @RequestHeader("X-Tenant-Id") Long tenantId) {
        return Result.ok(knowledgeBaseService.countDocuments(id, tenantId));
    }
}
