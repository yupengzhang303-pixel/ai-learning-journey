package com.zjjxy.sentiments.dto; // 修正了包名

import lombok.Data;

@Data
public class RiskDataDto {
    private String title;
    private String content;
    private String source;
    private String sentiment;
    private String level;
    private String reason;

    // 【新增】租户ID，用来隔离不同公司的数据
    private Long tenantId;
}