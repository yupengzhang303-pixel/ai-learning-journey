package com.zjjxy.config;

import com.zjjxy.filter.SensitiveWordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 敏感词初始化组件
 * 启动时将配置中的敏感词加载到Redis
 */
@Component
public class SensitiveWordInitConfig implements CommandLineRunner {

    private final SensitiveWordService sensitiveWordService;
    private final SensitiveWordsProperties sensitiveWordsProperties;

    public SensitiveWordInitConfig(SensitiveWordService sensitiveWordService,
                                   SensitiveWordsProperties sensitiveWordsProperties) {
        this.sensitiveWordService = sensitiveWordService;
        this.sensitiveWordsProperties = sensitiveWordsProperties;
    }

    @Override
    public void run(String... args) {
        System.out.println("===== 敏感词初始化开始 =====");
        System.out.println("检测到的初始敏感词: " + sensitiveWordsProperties.getWords());
        System.out.println("处理策略: " + sensitiveWordsProperties.getStrategy());

        var initWords = sensitiveWordsProperties.getWords();
        if (initWords != null && !initWords.isEmpty()) {
            // 先检查是否已有数据，避免重复添加
            if (sensitiveWordService.getWordCount() == 0) {
                long count = sensitiveWordService.addWords(Set.copyOf(initWords));
                System.out.println("===== 敏感词初始化完成，共加载 " + count + " 个词 =====");
            } else {
                System.out.println("===== 检测到已有敏感词数据，跳过初始化 =====");
            }
        } else {
            System.out.println("===== 未配置初始敏感词或配置为空 =====");
        }
        System.out.println("当前敏感词数量: " + sensitiveWordService.getWordCount());
    }
}
