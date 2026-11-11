package com.zjjxy.cache.service;

import com.zjjxy.tool.BloomFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ProductBloomService {

    @Autowired
    private BloomFilterHelper bloomFilter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getProduct(Long id) {
        String key = "product:" + id;

        // 1. 布隆过滤器判断：不存在则直接返回，不走数据库
        if (!bloomFilter.contains(key)) {
            System.out.println("布隆过滤器拦截>>>>>>>>");
            return null;
        }

        // 2. 可能存在，查缓存
        String cacheData = redisTemplate.opsForValue().get(key);
        if (cacheData != null) {
            System.out.println(">>>>Redis缓存命中>>>>>>>>>>>>");
            return cacheData;
        }

        // 3. 缓存没有，查数据库
        String dbValue = getByDB(id);
        if (dbValue != null) {
            redisTemplate.opsForValue().set(key, dbValue, Duration.ofMinutes(10));
        }
        return dbValue;
    }

    // 模拟数据库查询
    public String getByDB(Long id) {
        System.out.println("访问数据库>>>>>>>>>>>>");
        return (id >= 1 && id <= 1000) ? "小米汽车-" + id : null;
    }

    // 新增商品时，同步维护布隆过滤器
    public void addProduct(Long id) {
        // 1. 写数据库（省略）
        // 2. 同步添加到布隆过滤器，否则新 id 会被误判为不存在
        bloomFilter.add("product:" + id);
    }
}