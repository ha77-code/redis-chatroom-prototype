package com.chatroom.repository;

import com.chatroom.entity.User;
import com.chatroom.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

    private final StringRedisTemplate redis;

    public UserRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public User save(User user) {
        if (user.getUserId() == null) {
            user.setUserId("u" + redis.opsForValue().increment(RedisKeyUtil.USER_ID_COUNTER));
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }

        // save username → userId mapping
        redis.opsForHash().put(RedisKeyUtil.USER_BY_USERNAME, user.getUsername(), user.getUserId());

        // save user data
        String key = RedisKeyUtil.userKey(user.getUserId());
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("nickname", user.getNickname());
        map.put("role", user.getRole());
        map.put("status", user.getStatus());
        map.put("createTime", user.getCreateTime().toString());
        redis.opsForHash().putAll(key, map);
        return user;
    }

    public User findById(String userId) {
        String key = RedisKeyUtil.userKey(userId);
        Map<Object, Object> entries = redis.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return mapToUser(userId, entries);
    }

    public User findByUsername(String username) {
        Object userIdObj = redis.opsForHash().get(RedisKeyUtil.USER_BY_USERNAME, username);
        if (userIdObj == null) {
            return null;
        }
        return findById(userIdObj.toString());
    }

    public void update(User user) {
        String key = RedisKeyUtil.userKey(user.getUserId());
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("nickname", user.getNickname());
        map.put("role", user.getRole());
        map.put("status", user.getStatus());
        map.put("createTime", user.getCreateTime().toString());
        redis.opsForHash().putAll(key, map);
    }

    public boolean existsByUsername(String username) {
        return redis.opsForHash().hasKey(RedisKeyUtil.USER_BY_USERNAME, username);
    }

    private User mapToUser(String userId, Map<Object, Object> entries) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername((String) entries.get("username"));
        user.setPassword((String) entries.get("password"));
        user.setNickname((String) entries.get("nickname"));
        user.setRole((String) entries.get("role"));
        user.setStatus((String) entries.get("status"));
        user.setCreateTime(LocalDateTime.parse((String) entries.get("createTime")));
        return user;
    }
}
