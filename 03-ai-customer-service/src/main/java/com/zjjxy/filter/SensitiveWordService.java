package com.zjjxy.filter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词管理服务
 */
@Service
public class SensitiveWordService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "sensitive:words:";
    private static final String ALL_WORDS_KEY = KEY_PREFIX + "all";

    public SensitiveWordService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 添加敏感词
     */
    public boolean addWord(String word) {
        if (word == null || word.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.opsForSet().add(ALL_WORDS_KEY, word.trim()) > 0);
    }

    /**
     * 批量添加敏感词
     */
    public long addWords(Set<String> words) {
        if (words == null || words.isEmpty()) {
            return 0;
        }
        // 过滤空值并转为可变集合
        Set<String> filtered = new HashSet<>();
        for (String w : words) {
            if (w != null && !w.isBlank()) {
                filtered.add(w.trim());
            }
        }
        if (filtered.isEmpty()) {
            return 0;
        }
        return redisTemplate.opsForSet().add(ALL_WORDS_KEY, filtered.toArray(new String[0]));
    }

    /**
     * 删除敏感词
     */
    public boolean removeWord(String word) {
        if (word == null || word.isBlank()) {
            return false;
        }
        return redisTemplate.opsForSet().remove(ALL_WORDS_KEY, word) > 0;
    }

    /**
     * 查询所有敏感词
     */
    public Set<String> getAllWords() {
        return redisTemplate.opsForSet().members(ALL_WORDS_KEY);
    }

    /**
     * 获取敏感词数量
     */
    public long getWordCount() {
        Long size = redisTemplate.opsForSet().size(ALL_WORDS_KEY);
        return size != null ? size : 0;
    }

    /**
     * 判断词是否存在
     */
    public boolean exists(String word) {
        if (word == null || word.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ALL_WORDS_KEY, word.trim()));
    }

    /**
     * 清空所有敏感词
     */
    public void clearAll() {
        redisTemplate.delete(ALL_WORDS_KEY);
    }
}
