package com.zjjxy.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisChatMemory implements ChatMemory {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "chat:memory:";

    public RedisChatMemory(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = KEY_PREFIX + conversationId;
        for (Message message : messages) {
            String role = message.getMessageType().name();
            String content = message.getText();   // Spring AI 1.0.0 的 API
            StorageMessage sm = new StorageMessage(role, content);
            redis.opsForList().rightPush(key, toJson(sm));
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        String key = KEY_PREFIX + conversationId;
        List<String> jsonList = redis.opsForList().range(key, 0, -1);
        List<Message> result = new ArrayList<>();
        if (jsonList == null) return result;

        for (String json : jsonList) {
            try {
                StorageMessage sm = objectMapper.readValue(json, StorageMessage.class);
                result.add(toMessage(sm));
            } catch (Exception e) {
                System.err.println("解析消息失败: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public void clear(String conversationId) {
        redis.delete(KEY_PREFIX + conversationId);
    }

    private String toJson(StorageMessage sm) {
        try {
            return objectMapper.writeValueAsString(sm);
        } catch (Exception e) {
            throw new RuntimeException("序列化消息失败", e);
        }
    }

    private Message toMessage(StorageMessage sm) {
        MessageType type = MessageType.valueOf(sm.role());
        return switch (type) {
            case USER      -> new UserMessage(sm.content());
            case ASSISTANT -> new AssistantMessage(sm.content());
            case SYSTEM    -> new SystemMessage(sm.content());
            default        -> new UserMessage(sm.content());   // 兜底，防止编译报错
        };
    }

    public record StorageMessage(String role, String content) {}
}