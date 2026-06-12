package com.chatroom.repository;

import com.chatroom.entity.ChatRoom;
import com.chatroom.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RoomRepository {

    private final StringRedisTemplate redis;

    public RoomRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void save(ChatRoom room) {
        String key = RedisKeyUtil.roomMetaKey(room.getRoomId());
        Map<String, String> map = new HashMap<>();
        map.put("roomId", room.getRoomId());
        map.put("roomName", room.getRoomName());
        map.put("status", room.getStatus());
        map.put("notice", room.getNotice() != null ? room.getNotice() : "");
        map.put("createTime", room.getCreateTime().toString());
        redis.opsForHash().putAll(key, map);
    }

    public ChatRoom findById(String roomId) {
        String key = RedisKeyUtil.roomMetaKey(roomId);
        Map<Object, Object> entries = redis.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        ChatRoom room = new ChatRoom();
        room.setRoomId((String) entries.get("roomId"));
        room.setRoomName((String) entries.get("roomName"));
        room.setStatus((String) entries.get("status"));
        room.setNotice((String) entries.get("notice"));
        room.setCreateTime(LocalDateTime.parse((String) entries.get("createTime")));
        return room;
    }

    public boolean existsById(String roomId) {
        return Boolean.TRUE.equals(redis.hasKey(RedisKeyUtil.roomMetaKey(roomId)));
    }
}
