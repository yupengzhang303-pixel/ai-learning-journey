package com.zjjxy.cache.service;

//缓存击穿防护：分布式锁，只允许一个请求数据库溯源

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ProductLockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //查询热点商品
    public String getHotProduct(Long id) {
        String key = "product:" + id;          // redis存储的key
        String lockKey = "lock:product:" + id; // 锁单独用一个key

        //1. 先查缓存
        String cacheData = redisTemplate.opsForValue().get(key);
        if (cacheData != null) {
            return cacheData;
        }

        //2. 缓存没有，尝试加锁，只让一个请求查询数据库
        // setIfAbsent() 相当于redis中 set nx
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(10));

        if (Boolean.TRUE.equals(locked)) {
            try {
                //3. 拿到锁 去查数据库 写缓存
                // 保险起见，可以再查一次缓存，防止别的请求已经写好了
                cacheData = redisTemplate.opsForValue().get(key);
                if (cacheData != null) {
                    return cacheData;
                }

                //查数据库
                String dbData = queryDb(id);

                //回写缓存
                redisTemplate.opsForValue().set(key, dbData, Duration.ofMinutes(10));
                return dbData;
            } finally {
                //4. 释放锁
                redisTemplate.delete(lockKey);
            }
        } else {
            //5.没抢到锁，锁正在写入，稍后再试
            try {
                Thread.sleep(100);
                return redisTemplate.opsForValue().get(key);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //模拟数据库的查询
    public String queryDb(Long id) {
        return "商品- " + id;
    }
}