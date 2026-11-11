package com.zjjxy.cache.config;

import com.zjjxy.tool.BloomFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BloomFilterInitConfig implements ApplicationRunner {

    @Autowired
    private BloomFilterHelper bloomFilterHelper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 模拟从数据库加载已存在的商品 id，实际项目应查数据库
        for (long i = 1; i <= 1000; i++) {
            // 注意：这里必须和查询时使用的 key 格式一致
            bloomFilterHelper.add("product:" + i);
        }
        System.out.println("布隆过滤器初始化完成>>>");
    }
}