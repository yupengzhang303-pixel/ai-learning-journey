package com.zjjxy.controller;

/**
 * 请求体：Spring 自动把 JSON {"message":"..."} 绑定到 message 字段
 */
public record ChatRecord(String message) {
}