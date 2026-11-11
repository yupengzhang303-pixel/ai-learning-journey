package com.zjjxy.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ObjectRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public ObjectRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 直接存 Java 对象进 Redis
    public void cache(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 直接取对象并转换类型
    public <T> T get(String key, Class<T> clazz) {
        Object v = redisTemplate.opsForValue().get(key);
        return clazz.cast(v);
    }
}