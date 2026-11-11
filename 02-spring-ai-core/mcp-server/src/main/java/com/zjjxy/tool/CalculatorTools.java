package com.zjjxy.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    @Tool(description = "计算两个整数之和。当用户询问加法时调用。")
    public int add(
            @ToolParam(description = "第一个整数") int a,
            @ToolParam(description = "第二个整数") int b) {
        int result = a + b;
        System.out.println("【MCP Server】执行 add(" + a + ", " + b + ") = " + result);
        return result;
    }

    @Tool(description = "计算两个整数之差 (a - b)。当用户询问减法时调用。")
    public int subtract(
            @ToolParam(description = "被减数") int a,
            @ToolParam(description = "减数") int b) {
        int result = a - b;
        System.out.println("【MCP Server】执行 subtract(" + a + ", " + b + ") = " + result);
        return result;
    }
}