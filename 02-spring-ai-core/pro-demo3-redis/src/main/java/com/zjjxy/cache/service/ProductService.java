package com.zjjxy.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ProductService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String NULL_DATA = "null";

    public String getProduct(Long id) {
        String key = "product:" + id;

        // 1. 先查缓存
        String cacheData = redisTemplate.opsForValue().get(key);
        if (cacheData != null) {
            if (NULL_DATA.equals(cacheData)) {
                return null;
            }
            return cacheData;
        }

        // 2. 缓存未命中，查数据库
        String dbData = queryDB(id);

        // 3. 写缓存
        if (dbData == null) {
            // 数据库没有，缓存空值，设置较短过期时间，防止穿透
            redisTemplate.opsForValue().set(key, NULL_DATA, Duration.ofSeconds(60));
        } else {
            redisTemplate.opsForValue().set(key, dbData, Duration.ofMinutes(10));
        }
        return dbData;
    }

    // 模拟数据库查询
    private String queryDB(Long id) {
        // 假设数据库只有 id 1~1000 的商品
        if (id >= 1 && id <= 1000) {
            return "小米汽车-" + id;
        }
        return null;
    }
}