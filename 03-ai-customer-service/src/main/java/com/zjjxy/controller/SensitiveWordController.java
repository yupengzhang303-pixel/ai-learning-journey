package com.zjjxy.controller;

import com.zjjxy.common.Result;
import com.zjjxy.filter.SensitiveWordService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * 敏感词管理接口
 */
@RestController
@RequestMapping("/api/v1/sensitive-words")
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    public SensitiveWordController(SensitiveWordService sensitiveWordService) {
        this.sensitiveWordService = sensitiveWordService;
    }

    /**
     * 查询所有敏感词
     * GET /api/v1/sensitive-words
     */
    @GetMapping
    public Result<Map<String, Object>> getAll() {
        Set<String> words = sensitiveWordService.getAllWords();
        return Result.ok(Map.of(
                "words", words != null ? words : Set.of(),
                "count", sensitiveWordService.getWordCount()
        ));
    }

    /**
     * 添加敏感词
     * POST /api/v1/sensitive-words
     * Body: { "word": "测试" }
     */
    @PostMapping
    public Result<String> add(@RequestBody Map<String, String> request) {
        String word = request.get("word");
        if (word == null || word.isBlank()) {
            return Result.fail(400, "敏感词不能为空");
        }
        boolean added = sensitiveWordService.addWord(word);
        if (added) {
            return Result.ok("敏感词添加成功");
        } else {
            return Result.fail(400, "敏感词已存在");
        }
    }

    /**
     * 批量添加敏感词
     * POST /api/v1/sensitive-words/batch
     * Body: { "words": ["词1", "词2"] }
     */
    @PostMapping("/batch")
    public Result<Map<String, Object>> addBatch(@RequestBody Map<String, Set<String>> request) {
        Set<String> words = request.get("words");
        if (words == null || words.isEmpty()) {
            return Result.fail(400, "敏感词列表不能为空");
        }
        long count = sensitiveWordService.addWords(words);
        return Result.ok(Map.of(
                "added", count,
                "message", "成功添加 " + count + " 个敏感词"
        ));
    }

    /**
     * 删除敏感词
     * DELETE /api/v1/sensitive-words?word=测试
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam String word) {
        if (word == null || word.isBlank()) {
            return Result.fail(400, "敏感词不能为空");
        }
        boolean removed = sensitiveWordService.removeWord(word);
        if (removed) {
            return Result.ok("敏感词删除成功");
        } else {
            return Result.fail(400, "敏感词不存在");
        }
    }

    /**
     * 清空所有敏感词
     * DELETE /api/v1/sensitive-words/all
     */
    @DeleteMapping("/all")
    public Result<String> clearAll() {
        sensitiveWordService.clearAll();
        return Result.ok("已清空所有敏感词");
    }

    /**
     * 检查词是否存在
     * GET /api/v1/sensitive-words/check?word=测试
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> check(@RequestParam String word) {
        boolean exists = sensitiveWordService.exists(word);
        return Result.ok(Map.of(
                "word", word,
                "exists", exists
        ));
    }
}
