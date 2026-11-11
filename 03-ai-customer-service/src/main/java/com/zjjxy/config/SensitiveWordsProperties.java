package com.zjjxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词配置属性类
 */
@Component
@ConfigurationProperties(prefix = "sensitive")
public class SensitiveWordsProperties {

    private List<String> words = new ArrayList<>();
    private String strategy = "reject";

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
