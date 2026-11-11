package com.zjjxy.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ChatMemoryConfig {

    private static final String KEY_PREFIX = "chat:memory:";

    @Bean
    public ChatMemory chatMemory(StringRedisTemplate stringRedisTemplate) {
        return new RedisChatMemory(stringRedisTemplate);
    }

    @Bean
    public ChatClient memoryChatClient(ChatClient.Builder builder, ChatMemory memory) {
        return builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build()
                )
                .build();
    }

    /**
     * 自定义 RedisChatMemory 实现
     * 使用 StringRedisTemplate（Lettuce 客户端），不依赖 Jedis
     */
    public static class RedisChatMemory implements ChatMemory {

        private final StringRedisTemplate redisTemplate;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public RedisChatMemory(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public void add(String conversationId, List<Message> messages) {
            String key = KEY_PREFIX + conversationId;
            for (Message message : messages) {
                try {
                    String json = objectMapper.writeValueAsString(message);
                    redisTemplate.opsForList().rightPush(key, json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("序列化消息失败: " + message, e);
                }
            }
        }

        @Override
        public List<Message> get(String conversationId) {
            String key = KEY_PREFIX + conversationId;
            List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
            if (jsonList == null || jsonList.isEmpty()) {
                return new ArrayList<>();
            }

            List<Message> messages = new ArrayList<>();
            for (String json : jsonList) {
                try {
                    messages.add(parseMessage(json));
                } catch (Exception e) {
                    throw new RuntimeException("反序列化消息失败: " + json, e);
                }
            }
            return messages;
        }

        @Override
        public void clear(String conversationId) {
            redisTemplate.delete(KEY_PREFIX + conversationId);
        }

        private Message parseMessage(String json) throws com.fasterxml.jackson.databind.JsonMappingException, JsonProcessingException {
            if (json.contains("\"type\":\"USER\"")) {
                return objectMapper.readValue(json, UserMessage.class);
            } else if (json.contains("\"type\":\"ASSISTANT\"")) {
                return objectMapper.readValue(json, AssistantMessage.class);
            } else if (json.contains("\"type\":\"SYSTEM\"")) {
                return objectMapper.readValue(json, SystemMessage.class);
            } else {
                return objectMapper.readValue(json, UserMessage.class);
            }
        }
    }
}
