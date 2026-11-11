package com.zjjxy.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisChatMemory implements ChatMemory {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // key 前缀：chat:memory:{conversationId}
    private static final String KEY_PREFIX = "chat:memory:";

    // 构造器注入
    public RedisChatMemory(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * ① 追加历史：把本轮的「用户问 + 模型答」一批消息逐条转 JSON，追加到 List 尾部
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = KEY_PREFIX + conversationId;
        for (Message m : messages) {
            // getMessageType().name() 得到 "USER"/"ASSISTANT"/"SYSTEM"
            StoredMessage sm = new StoredMessage(m.getMessageType().name(), m.getText());
            // rightPush: 往 List 尾部追加 JSON 字符串（对应 Redis 的 RPUSH）
            redisTemplate.opsForList().rightPush(key, toJson(sm));
        }
    }

    /**
     * ② 读取历史：取出该会话的全部消息，并按角色还原成正确的消息类型
     */
    @Override
    public List<Message> get(String conversationId) {
        List<String> jsons = redisTemplate.opsForList()
                .range(KEY_PREFIX + conversationId, 0, -1);
        List<Message> messages = new ArrayList<>();
        if (jsons != null) {
            for (String json : jsons) {
                messages.add(toMessage(fromJson(json)));
            }
        }
        return messages;
    }

    /**
     * ③ 清空会话：删除整个 key
     */
    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(KEY_PREFIX + conversationId);
    }

    // 按「角色」还原成正确的具体类型
    private Message toMessage(StoredMessage sm) {
        return switch (sm.type()) {
            case "ASSISTANT" -> new AssistantMessage(sm.text());
            case "SYSTEM"    -> new SystemMessage(sm.text());
            default          -> new UserMessage(sm.text());
        };
    }

    // Java 对象 → JSON 字符串
    private String toJson(StoredMessage sm) {
        try {
            return objectMapper.writeValueAsString(sm);
        } catch (Exception e) {
            throw new RuntimeException("序列化消息失败", e);
        }
    }

    // JSON 字符串 → Java 对象
    private StoredMessage fromJson(String json) {
        try {
            return objectMapper.readValue(json, StoredMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("反序列化消息失败", e);
        }
    }

    // 存进 Redis 的最小单元
    public record StoredMessage(String type, String text) {}
}