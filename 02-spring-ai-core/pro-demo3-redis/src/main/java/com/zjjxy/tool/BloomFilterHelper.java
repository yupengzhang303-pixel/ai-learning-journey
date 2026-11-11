package com.zjjxy.tool;

import jakarta.annotation.PostConstruct;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BloomFilterHelper {

    @Autowired
    private RedissonClient redissonClient;

    private RBloomFilter<String> bloomFilter;

    @PostConstruct
    public void init() {
        // 获取或创建名为 "product:bloom" 的布隆过滤器
        this.bloomFilter = redissonClient.getBloomFilter("product:bloom");
        // 预计 10 万个元素，误判率 3%
        this.bloomFilter.tryInit(100000L, 0.03);
    }

    public void add(String value) {
        bloomFilter.add(value);
    }

    public boolean contains(String value) {
        return bloomFilter.contains(value);
    }
}