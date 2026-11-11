package com.zjjxy.dto;

import lombok.Data;

@Data
public class IntentResult {
    private String intent;       // 意图：咨询/投诉/建议
    private String summary;      // 一句话概括
    private int confidence;      // 置信度
}