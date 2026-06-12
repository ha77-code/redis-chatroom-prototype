package com.chatroom.repository;

import com.chatroom.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
public class OnlineUserRepository {

    private final StringRedisTemplate redis;

    public OnlineUserRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void addOnlineUser(String userId, String roomId) {
        redis.opsForSet().add(RedisKeyUtil.ONLINE_USERS_SET, userId);

        String key = RedisKeyUtil.userStatusKey(userId);
        Map<String, String> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("online", "true");
        map.put("lastActiveTime", LocalDateTime.now().toString());
        redis.opsForHash().putAll(key, map);
    }

    public void removeOnlineUser(String userId) {
        redis.opsForSet().remove(RedisKeyUtil.ONLINE_USERS_SET, userId);
        redis.opsForHash().put(RedisKeyUtil.userStatusKey(userId), "online", "false");
    }

    public Set<String> getOnlineUserIds() {
        return redis.opsForSet().members(RedisKeyUtil.ONLINE_USERS_SET);
    }

    public String getUserRoomId(String userId) {
        Object roomId = redis.opsForHash().get(RedisKeyUtil.userStatusKey(userId), "roomId");
        return roomId != null ? roomId.toString() : null;
    }

    public boolean isOnline(String userId) {
        return Boolean.TRUE.equals(redis.opsForSet().isMember(RedisKeyUtil.ONLINE_USERS_SET, userId));
    }

    public void updateLastActiveTime(String userId) {
        redis.opsForHash().put(RedisKeyUtil.userStatusKey(userId), "lastActiveTime", LocalDateTime.now().toString());
    }
}
