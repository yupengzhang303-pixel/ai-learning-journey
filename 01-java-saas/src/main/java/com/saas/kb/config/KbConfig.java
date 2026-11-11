package com.saas.kb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "kb")
public class KbConfig {
    /**
     * 默认分页大小
     */
    private Integer defaultPageSize = 10;
    /**
     * 最大分页大小
     */
    private Integer maxPageSize = 100;
}
