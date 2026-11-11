package com.zjjxy.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HashRedisService {

    private final StringRedisTemplate redisTemplate;

    public HashRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 一次性存一个对象（多个字段）
    public void saveUser(String key, Map<String, String> fields) {
        redisTemplate.opsForHash().putAll(key, fields);
    }

    // 取单个字段
    public String getField(String key, String field) {
        Object v = redisTemplate.opsForHash().get(key, field);
        return v == null ? null : v.toString();
    }

    // 取整个 Hash
    public Map<Object, Object> getAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
}