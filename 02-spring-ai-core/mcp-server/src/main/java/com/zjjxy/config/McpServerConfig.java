package com.zjjxy.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动扫描 com.zjjxy.tool 包下所有带 @Tool 方法的 Bean，统一注册为 MCP 工具
 */
@Configuration
public class McpServerConfig {

    private static final String TOOL_PACKAGE = "com.zjjxy.tool";

    @Bean
    public ToolCallbackProvider mcpToolProvider(ApplicationContext ctx) {
        List<Object> toolObjects = new ArrayList<>();

        for (String beanName : ctx.getBeanDefinitionNames()) {
            Class<?> type = ctx.getType(beanName);
            if (type == null) continue;
            if (!type.getPackageName().startsWith(TOOL_PACKAGE)) continue;
            if (!hasToolMethod(type)) continue;

            try {
                toolObjects.add(ctx.getBean(beanName));
                System.out.println("【MCP Server】发现工具类: " + type.getSimpleName());
            } catch (Exception e) {
                System.err.println("【MCP Server】Bean 创建失败: " + beanName + " - " + e.getMessage());
            }
        }

        System.out.println("【MCP Server】共注册 " + toolObjects.size() + " 个工具类");

        return MethodToolCallbackProvider.builder()
                .toolObjects(toolObjects.toArray())
                .build();
    }

    private boolean hasToolMethod(Class<?> clazz) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Tool.class)) return true;
        }
        return false;
    }
}