package com.zjjxy.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class WeatherTools {

    @Tool(description = "查询指定城市的实时天气。当用户询问某地天气、气温、下雨、晴雨等情况时调用。")
    public String getWeather(
            @ToolParam(description = "城市名，例如：杭州、北京、上海") String city) {
        System.out.println("【MCP Server】执行 getWeather(" + city + ")");

        return switch (city) {
            case "杭州" -> "杭州今天晴，25℃，微风，适合出行。";
            case "北京" -> "北京今天多云，18℃，北风3级，比较凉爽。";
            case "上海" -> "上海今天小雨，22℃，记得带伞。";
            case "广州" -> "广州今天雷阵雨，30℃，闷热潮湿。";
            case "成都" -> "成都今天阴，20℃，湿度较大。";
            default    -> city + "的天气数据暂时无法获取，请稍后再试。";
        };
    }

    @Tool(description = "查询指定城市指定日期的天气预报。当用户询问明天/后天的天气时调用。")
    public String getWeatherByDate(
            @ToolParam(description = "城市名，例如：杭州") String city,
            @ToolParam(description = "日期，例如：2026-09-24") String date) {
        System.out.println("【MCP Server】执行 getWeatherByDate(" + city + ", " + date + ")");
        return city + "在" + date + "的天气：多云，22℃，适合外出。";
    }
}