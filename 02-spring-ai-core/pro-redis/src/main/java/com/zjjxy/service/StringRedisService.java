package com.zjjxy.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class StringRedisService {

    private final StringRedisTemplate redisTemplate;

    // 构造器注入
    public StringRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 存字符串，10 分钟过期
    public void cache(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
    }

    // 取字符串
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 原子自增
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // SETNX：key 不存在才设置（分布式锁基础）
    public boolean tryLock(String key, String value) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, value);
        return Boolean.TRUE.equals(ok);
    }
}