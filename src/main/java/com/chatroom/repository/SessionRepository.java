package com.chatroom.repository;

import com.chatroom.entity.SessionInfo;
import com.chatroom.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class SessionRepository {

    private final StringRedisTemplate redis;
    private final int sessionExpireMinutes;

    public SessionRepository(StringRedisTemplate redis,
                             @Value("${chatroom.session.expire-minutes:30}") int sessionExpireMinutes) {
        this.redis = redis;
        this.sessionExpireMinutes = sessionExpireMinutes;
    }

    public void save(SessionInfo session) {
        String key = RedisKeyUtil.sessionKey(session.getToken());
        Map<String, String> map = new HashMap<>();
        map.put("userId", session.getUserId());
        map.put("username", session.getUsername());
        map.put("role", session.getRole());
        map.put("loginTime", session.getLoginTime().toString());
        map.put("expireAt", session.getExpireAt().toString());
        redis.opsForHash().putAll(key, map);
        redis.expire(key, sessionExpireMinutes, TimeUnit.MINUTES);
    }

    public SessionInfo findByToken(String token) {
        String key = RedisKeyUtil.sessionKey(token);
        Map<Object, Object> entries = redis.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        SessionInfo session = new SessionInfo();
        session.setToken(token);
        session.setUserId((String) entries.get("userId"));
        session.setUsername((String) entries.get("username"));
        session.setRole((String) entries.get("role"));
        session.setLoginTime(LocalDateTime.parse((String) entries.get("loginTime")));
        session.setExpireAt(LocalDateTime.parse((String) entries.get("expireAt")));
        return session;
    }

    public void deleteByToken(String token) {
        redis.delete(RedisKeyUtil.sessionKey(token));
    }

    public void refreshExpiry(String token) {
        redis.expire(RedisKeyUtil.sessionKey(token), sessionExpireMinutes, TimeUnit.MINUTES);
    }
}
