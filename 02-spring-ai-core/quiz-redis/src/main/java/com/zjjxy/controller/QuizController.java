package com.zjjxy.controller;

import com.zjjxy.service.QuizRedisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizRedisService quizService;

    public QuizController(QuizRedisService quizService) {
        this.quizService = quizService;
    }

    /**
     * 用户答一题
     * POST /api/quiz/answer
     * Body: {"userId":"u1001", "questionId":"q1", "correct":true, "score":10}
     */
    @PostMapping("/answer")
    public Map<String, Object> answer(@RequestBody Map<String, Object> body) {
        String userId     = (String) body.get("userId");
        String questionId = (String) body.get("questionId");
        Boolean correct   = (Boolean) body.get("correct");
        Number scoreNum   = (Number) body.get("score");
        double score = scoreNum == null ? 10 : scoreNum.doubleValue();
        return quizService.answerOne(userId, questionId,
                correct != null && correct, score);
    }

    /**
     * 查询某人积分和排名
     * GET /api/quiz/score?userId=u1001
     */
    @GetMapping("/score")
    public Map<String, Object> getScore(@RequestParam String userId) {
        Double score = quizService.getUserScore(userId);
        Long rank = quizService.getUserRank(userId);
        return Map.of(
                "userId", userId,
                "score", score == null ? 0 : score,
                "rank", rank == null ? -1 : rank  // -1 表示没上榜
        );
    }

    /**
     * Top N 排行榜
     * GET /api/quiz/top?n=5
     */
    @GetMapping("/top")
    public List<Map<String, Object>> top(@RequestParam(defaultValue = "5") int n) {
        return quizService.getTopN(n);
    }

    /**
     * 查询某人答题次数
     * GET /api/quiz/count?userId=u1001
     */
    @GetMapping("/count")
    public Map<String, Object> count(@RequestParam String userId) {
        return Map.of(
                "userId", userId,
                "answerCount", quizService.getAnswerCount(userId)
        );
    }

    /**
     * 查询某人最近 5 条答题记录
     * GET /api/quiz/recent?userId=u1001
     */
    @GetMapping("/recent")
    public List<String> recent(@RequestParam String userId) {
        return quizService.getRecent(userId);
    }
}