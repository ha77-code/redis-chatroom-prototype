package com.chatroom.repository;

import com.chatroom.entity.Message;
import com.chatroom.util.RedisKeyUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class MessageRepository {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public MessageRepository(StringRedisTemplate redis) {
        this.redis = redis;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void save(Message message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redis.opsForList().rightPush(RedisKeyUtil.roomMessagesKey(message.getRoomId()), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize message", e);
        }
    }

    public List<Message> findMessagesAfter(String roomId, String lastMessageId) {
        String key = RedisKeyUtil.roomMessagesKey(roomId);
        List<String> all = redis.opsForList().range(key, 0, -1);
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }

        List<Message> result = new ArrayList<>();
        int startIndex = -1;

        if (lastMessageId != null && !lastMessageId.isEmpty()) {
            for (int i = all.size() - 1; i >= 0; i--) {
                Message msg = deserialize(all.get(i));
                if (msg != null && lastMessageId.equals(msg.getMessageId())) {
                    startIndex = i;
                    break;
                }
            }
        }

        int from = startIndex == -1 ? Math.max(0, all.size() - 50) : startIndex + 1;
        for (int i = from; i < all.size(); i++) {
            Message msg = deserialize(all.get(i));
            if (msg != null) {
                result.add(msg);
            }
        }
        return result;
    }

    public List<Message> findHistory(String roomId, int offset, int limit) {
        String key = RedisKeyUtil.roomMessagesKey(roomId);
        long size = redis.opsForList().size(key);
        long start = Math.max(0, size - offset - limit);
        long end = size - offset - 1;
        if (end < 0 || start > end) {
            return Collections.emptyList();
        }

        List<String> raw = redis.opsForList().range(key, start, end);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }

        List<Message> result = new ArrayList<>();
        for (String json : raw) {
            Message msg = deserialize(json);
            if (msg != null) {
                result.add(msg);
            }
        }
        return result;
    }

    public boolean markDeleted(String roomId, String messageId) {
        String key = RedisKeyUtil.roomMessagesKey(roomId);
        List<String> all = redis.opsForList().range(key, 0, -1);
        if (all == null) {
            return false;
        }
        for (int i = 0; i < all.size(); i++) {
            Message msg = deserialize(all.get(i));
            if (msg != null && messageId.equals(msg.getMessageId())) {
                msg.setDeleted(true);
                try {
                    String updated = objectMapper.writeValueAsString(msg);
                    redis.opsForList().set(key, i, updated);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("failed to update message", e);
                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteByAdmin(String roomId, String messageId, String adminId) {
        return markDeleted(roomId, messageId);
    }

    private Message deserialize(String json) {
        try {
            return objectMapper.readValue(json, Message.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
