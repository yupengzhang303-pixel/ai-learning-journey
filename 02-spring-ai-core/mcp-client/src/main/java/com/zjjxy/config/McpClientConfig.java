package com.zjjxy.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 手动创建 MCP Client，带 Bearer Token 认证
 */
@Configuration
public class McpClientConfig {

    @Value("${mcp.server.base-url}")
    private String baseUrl;

    @Value("${mcp.server.sse-endpoint}")
    private String sseEndpoint;

    @Value("${mcp.server.token}")
    private String token;

    @Bean(destroyMethod = "close")
    public McpSyncClient mcpSyncClient() {
        System.out.println("【MCP Client】连接 Server: " + baseUrl + sseEndpoint);

        McpSyncClient client = McpClient
                .sync(
                        HttpClientSseClientTransport.builder(baseUrl)
                                .sseEndpoint(sseEndpoint)
                                .customizeRequest(builder ->
                                        builder.header("Authorization", "Bearer " + token))
                                .build()
                )
                .build();

        client.initialize();
        System.out.println("【MCP Client】握手成功，工具数: "
                + client.listTools().tools().size());
        return client;
    }

    @Bean
    public ToolCallbackProvider mcpTools(McpSyncClient mcpClient) {
        return new SyncMcpToolCallbackProvider(List.of(mcpClient));
    }
}