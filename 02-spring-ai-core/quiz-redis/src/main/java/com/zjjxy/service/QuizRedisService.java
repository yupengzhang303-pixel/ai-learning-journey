package com.zjjxy.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizRedisService {

    private final StringRedisTemplate redis;

    // ============ Redis Key 统一管理 ============
    private static final String RANK_KEY         = "quiz:rank";               // 排行榜 ZSet
    private static final String COUNT_KEY_FMT    = "quiz:count:%s";           // 答题次数 String
    private static final String RECENT_KEY_FMT   = "quiz:recent:%s";          // 最近答题记录 List
    private static final int    RECENT_MAX       = 5;                         // 只留最近 5 条

    public QuizRedisService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    // =====================================================
    // 场景 1：排行榜（ZSet）
    // =====================================================

    /**
     * 用户答对一题，给用户加积分，同时更新排行榜
     * 底层：ZINCRBY quiz:rank <score> <userId>   —— 原子操作
     */
    public double addScore(String userId, double score) {
        Double total = redis.opsForZSet().incrementScore(RANK_KEY, userId, score);
        return total == null ? 0 : total;
    }

    /**
     * 查询某人当前积分
     */
    public Double getUserScore(String userId) {
        return redis.opsForZSet().score(RANK_KEY, userId);
    }

    /**
     * 查询某人当前排名（从 0 开始，0 表示第一名）
     */
    public Long getUserRank(String userId) {
        return redis.opsForZSet().reverseRank(RANK_KEY, userId);
    }

    /**
     * Top N 排行榜
     * 返回：[{userId, score}, ...] 按 score 从高到低
     */
    public List<Map<String, Object>> getTopN(int n) {
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redis.opsForZSet().reverseRangeWithScores(RANK_KEY, 0, n - 1);
        List<Map<String, Object>> result = new ArrayList<>();
        if (tuples == null) return result;

        int rank = 1;
        for (ZSetOperations.TypedTuple<String> t : tuples) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rank", rank++);
            item.put("userId", t.getValue());
            item.put("score", t.getScore());
            result.add(item);
        }
        return result;
    }

    // =====================================================
    // 场景 2：答题计数器（String INCR）
    // =====================================================

    /**
     * 用户答题一次，计数 +1
     * 底层：INCR quiz:count:<userId>   —— 原子操作
     */
    public long incrementAnswerCount(String userId) {
        String key = String.format(COUNT_KEY_FMT, userId);
        Long count = redis.opsForValue().increment(key);
        return count == null ? 0 : count;
    }

    /**
     * 查询某人答题次数
     */
    public long getAnswerCount(String userId) {
        String key = String.format(COUNT_KEY_FMT, userId);
        String v = redis.opsForValue().get(key);
        return v == null ? 0 : Long.parseLong(v);
    }

    // =====================================================
    // 场景 3：最近答题记录（List + LTRIM）
    // =====================================================

    /**
     * 追加一条答题记录，并只保留最近 5 条
     * 底层：LPUSH quiz:recent:<userId> <record>
     *       LTRIM quiz:recent:<userId> 0 4
     */
    public void pushRecent(String userId, String record) {
        String key = String.format(RECENT_KEY_FMT, userId);
        // 1. 左侧压入最新记录
        redis.opsForList().leftPush(key, record);
        // 2. 只保留 [0, RECENT_MAX-1] 区间，其他全部裁掉
        redis.opsForList().trim(key, 0, RECENT_MAX - 1);
    }

    /**
     * 查询最近 5 条答题记录（最新在前）
     */
    public List<String> getRecent(String userId) {
        String key = String.format(RECENT_KEY_FMT, userId);
        List<String> list = redis.opsForList().range(key, 0, RECENT_MAX - 1);
        return list == null ? Collections.emptyList() : list;
    }

    // =====================================================
    // 综合：答题一次，三个场景一起更新
    // =====================================================

    /**
     * 用户答题一次（核心业务方法）
     * 一次性完成：
     *   1. 排行榜加分（ZSet）
     *   2. 答题次数 +1（String INCR）
     *   3. 写入最近记录（List LPUSH + LTRIM）
     */
    public Map<String, Object> answerOne(String userId, String questionId,
                                          boolean correct, double score) {
        // 1. 排行榜加分
        if (correct) {
            addScore(userId, score);
        }

        // 2. 答题次数 +1
        long count = incrementAnswerCount(userId);

        // 3. 写入最近记录
        String record = String.format("题目[%s] %s (得分 %.0f)",
                questionId, correct ? "答对" : "答错", correct ? score : 0.0);
        pushRecent(userId, record);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", userId);
        result.put("correct", correct);
        result.put("totalScore", getUserScore(userId));
        result.put("answerCount", count);
        result.put("recent", getRecent(userId));
        return result;
    }

    // =====================================================
    // 工具：清空某人的数据（方便测试）
    // =====================================================
    public void clearUser(String userId) {
        redis.delete(RANK_KEY);
        redis.delete(String.format(COUNT_KEY_FMT, userId));
        redis.delete(String.format(RECENT_KEY_FMT, userId));
    }
}