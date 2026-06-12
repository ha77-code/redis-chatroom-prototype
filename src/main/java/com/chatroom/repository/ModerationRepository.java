package com.chatroom.repository;

import com.chatroom.entity.MuteRecord;
import com.chatroom.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Repository
public class ModerationRepository {

    private final StringRedisTemplate redis;

    public ModerationRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void muteUser(String roomId, String userId, int durationMinutes) {
        String key = RedisKeyUtil.muteKey(roomId, userId);
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(durationMinutes);
        redis.opsForValue().set(key, expireAt.toString(), durationMinutes, TimeUnit.MINUTES);
    }

    public MuteRecord getMuteRecord(String roomId, String userId) {
        String key = RedisKeyUtil.muteKey(roomId, userId);
        String value = redis.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        LocalDateTime expireAt = LocalDateTime.parse(value);
        if (expireAt.isBefore(LocalDateTime.now())) {
            redis.delete(key);
            return null;
        }
        return new MuteRecord(roomId, userId, expireAt);
    }

    public boolean isMuted(String roomId, String userId) {
        return getMuteRecord(roomId, userId) != null;
    }

    public void unmuteUser(String roomId, String userId) {
        redis.delete(RedisKeyUtil.muteKey(roomId, userId));
    }

    public void kickUser(String roomId, String userId) {
        String key = RedisKeyUtil.kickKey(roomId, userId);
        redis.opsForValue().set(key, LocalDateTime.now().toString());
    }

    public boolean isKicked(String roomId, String userId) {
        return Boolean.TRUE.equals(redis.hasKey(RedisKeyUtil.kickKey(roomId, userId)));
    }

    public void removeKick(String roomId, String userId) {
        redis.delete(RedisKeyUtil.kickKey(roomId, userId));
    }
}
