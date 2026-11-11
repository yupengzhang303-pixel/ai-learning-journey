package com.zjjxy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * MCP Server 认证过滤器：校验 Authorization 请求头
 * - 正确格式：Authorization: Bearer my-secret-token
 * - 不带或错误：返回 401 Unauthorized
 */
@Component
public class McpAuthFilter extends OncePerRequestFilter {

    @Value("${mcp.auth.token}")
    private String expectedToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        System.out.println("【MCP Auth】收到请求: " + request.getRequestURI()
                + "，Authorization = " + auth);

        // 校验：必须携带 "Bearer <token>"
        if (auth == null || !auth.trim().equals("Bearer " + expectedToken)) {
            System.err.println("【MCP Auth】认证失败，返回 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);   // 401
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Unauthorized");
            return;
        }

        System.out.println("【MCP Auth】认证通过");
        filterChain.doFilter(request, response);
    }
}