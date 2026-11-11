package com.zjjxy.sentiments.service; // 修正了包名

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjjxy.sentiments.dto.RiskDataDto; // 修正了包名
import com.zjjxy.sentiments.entity.SentimentRecord; // 修正了包名
import com.zjjxy.sentiments.mapper.SentimentRecordMapper; // 修正了包名
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentService {

    private final SentimentRecordMapper recordMapper;

    public void processAndSave(RiskDataDto dto) {
        // 1. 用 Title 做防重（防重也要带上租户ID，确保不同公司同名新闻互不影响）
        Long tenantId = dto.getTenantId() == null ? 1L : dto.getTenantId();
        Long count = recordMapper.selectCount(new LambdaQueryWrapper<SentimentRecord>()
                .eq(SentimentRecord::getTitle, dto.getTitle())
                .eq(SentimentRecord::getTenantId, tenantId));

        if (count > 0) {
            log.warn("新闻 [{}] 已存在，跳过处理", dto.getTitle());
            return;
        }

        // 2. 落库
        SentimentRecord record = new SentimentRecord();

        // 【修改】处理租户ID：如果 N8N 没传，默认给 1L（代表公司A）
        record.setTenantId(tenantId);

        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setSentiment(dto.getSentiment());
        record.setLevel(dto.getLevel());
        record.setSource(dto.getSource());
        record.setReason(dto.getReason());
        record.setCreateTime(LocalDateTime.now());

        recordMapper.insert(record);
        log.info("新闻落库成功: [{}], 级别: {}, 租户ID: {}", record.getTitle(), record.getLevel(), record.getTenantId());
    }

    // 【新增】按租户查询列表，供 Python 生成日报使用
    public List<SentimentRecord> getListByTenant(Long tenantId) {
        return recordMapper.selectList(new LambdaQueryWrapper<SentimentRecord>()
                .eq(SentimentRecord::getTenantId, tenantId)
                .orderByDesc(SentimentRecord::getCreateTime) // 按时间倒序，最新的在最前
        );
    }
}