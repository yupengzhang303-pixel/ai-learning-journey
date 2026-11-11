package com.zjjxy.sentiments.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sentiment_record")
public class SentimentRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String title;
    private String content;
    private String sentiment;
    private String level;
    private String source;
    private String reason;
    private LocalDateTime createTime;
}