package com.zjjxy.faq;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FaqMatcher {

    // 精确匹配
    private static final Map<String, String> EXACT_FAQ = new LinkedHashMap<>();
    // 关键词匹配
    private static final Map<String, String> KEYWORD_FAQ = new LinkedHashMap<>();

    static {
        EXACT_FAQ.put("你好", "你好呀！我是小知，很高兴为你服务～");
        EXACT_FAQ.put("hi",   "Hi~ 我是小知，有什么可以帮你？");
        EXACT_FAQ.put("在吗", "在的！随时为你服务～");
        EXACT_FAQ.put("谢谢", "不客气！有需要随时找我～");
        EXACT_FAQ.put("再见", "再见！祝你生活愉快～");

        KEYWORD_FAQ.put("年假",   "📅 年假政策：入职满一年5天年假，随工龄每年递增1天，上限15天。");
        KEYWORD_FAQ.put("退款",   "💰 退款政策：支持7天无理由退款，联系客服即可办理。");
        KEYWORD_FAQ.put("发票",   "🧾 发票申请：付款后可在「个人中心-发票管理」在线申请电子发票。");
        KEYWORD_FAQ.put("人工客服", "📞 人工客服：请拨打 400-800-1234（9:00-21:00）。");
        KEYWORD_FAQ.put("工作时间", "🕘 服务时间：9:00-21:00，其他时间可留言。");
        KEYWORD_FAQ.put("价格",   "💵 价格详情请查看官网「定价」页面，新用户首月8折。");
    }

    /**
     * 匹配 FAQ
     * @return 命中的答案；未命中返回 null
     */
    public String match(String question) {
        if (question == null || question.isBlank()) return null;
        String q = question.trim().toLowerCase();

        // 1. 精确匹配
        String exact = EXACT_FAQ.get(q);
        if (exact != null) return exact;

        // 2. 关键词匹配
        for (Map.Entry<String, String> entry : KEYWORD_FAQ.entrySet()) {
            if (q.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }
}