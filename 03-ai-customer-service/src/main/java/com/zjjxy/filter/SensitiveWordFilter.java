package com.zjjxy.filter;

import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感词过滤器
 * 支持精确匹配和单个字通配符模糊匹配
 */
@Component
public class SensitiveWordFilter {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "sensitive:words:";

    // 匹配策略枚举
    public enum Strategy {
        REJECT,  // 拒绝
        REPLACE, // 替换
        LOG      // 放行但记录
    }

    public SensitiveWordFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检测并处理敏感词
     * @param text 待检测文本
     * @param strategy 处理策略
     * @return 处理结果，包含是否通过和替换后的文本
     */
    public FilterResult filter(String text, Strategy strategy) {
        System.out.println("[SensitiveWordFilter] 开始过滤，文本: " + text + "，策略: " + strategy);
        if (text == null || text.isBlank()) {
            return new FilterResult(true, text);
        }

        // 获取所有敏感词
        Set<String> words = redisTemplate.opsForSet().members(KEY_PREFIX + "all");
        System.out.println("[SensitiveWordFilter] 从Redis获取到敏感词: " + words);
        if (words == null || words.isEmpty()) {
            System.out.println("[SensitiveWordFilter] 敏感词库为空，放行");
            return new FilterResult(true, text);
        }

        StringBuilder result = new StringBuilder(text);
        boolean found = false;

        for (String word : words) {
            if (word == null || word.isBlank()) continue;

            if (word.contains("*")) {
                // 模糊匹配：* 匹配任意单个字
                String regex = word.replace("*", ".");
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    found = true;
                    System.out.println("[SensitiveWordFilter] 模糊匹配命中: " + word);
                    if (strategy == Strategy.REPLACE) {
                        result = new StringBuilder(
                            matcher.replaceAll(mask(word.length()))
                        );
                    }
                }
            } else {
                // 精确匹配
                if (text.contains(word)) {
                    found = true;
                    System.out.println("[SensitiveWordFilter] 精确匹配命中: " + word);
                    if (strategy == Strategy.REPLACE) {
                        result = new StringBuilder(
                            text.replace(word, mask(word.length()))
                        );
                    }
                }
            }
        }

        if (!found) {
            System.out.println("[SensitiveWordFilter] 未命中敏感词，放行");
            return new FilterResult(true, text);
        }

        System.out.println("[SensitiveWordFilter] 命中敏感词，执行策略: " + strategy);
        return switch (strategy) {
            case REJECT -> new FilterResult(false, "消息包含敏感词，请修改后重试");
            case REPLACE -> new FilterResult(true, result.toString());
            case LOG -> new FilterResult(true, text); // 放行
        };
    }

    /**
     * 简单检测是否含敏感词（不处理）
     */
    public boolean containsSensitive(String text) {
        if (text == null || text.isBlank()) return false;

        Set<String> words = redisTemplate.opsForSet().members(KEY_PREFIX + "all");
        if (words == null || words.isEmpty()) return false;

        for (String word : words) {
            if (word == null || word.isBlank()) continue;

            if (word.contains("*")) {
                String regex = word.replace("*", ".");
                if (Pattern.compile(regex).matcher(text).find()) {
                    return true;
                }
            } else {
                if (text.contains(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String mask(int length) {
        return "*".repeat(Math.max(length, 1));
    }

    public record FilterResult(boolean passed, String message) {}
}
