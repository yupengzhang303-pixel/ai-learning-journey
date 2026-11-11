package com.zjjxy.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherTools {

    /**
     * 查询天气的工具
     * description 极其重要 —— 模型完全靠它判断"什么时候该调用这个工具"
     */
    @Tool(description = "查询指定城市的实时天气，当用户询问天气时调用。")
    public String getWeather(@ToolParam(description = "城市名，例如：杭州") String city) {
        // 模拟数据（生产环境应接入真实天气 API）
        return city + "今天晴，25℃，适合出行";
    }

    /**
     * 获取当前时间的工具（PDF 里的练习题）
     */
    @Tool(description = "获取当前时间，当用户询问时间或日期时调用。")
    public String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "当前时间是：" + LocalDateTime.now().format(formatter);
    }
}