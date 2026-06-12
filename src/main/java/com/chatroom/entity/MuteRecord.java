package com.chatroom.entity;

import java.time.LocalDateTime;

public class MuteRecord {

    private String roomId;
    private String userId;
    private LocalDateTime expireAt;

    public MuteRecord() {
    }

    public MuteRecord(String roomId, String userId, LocalDateTime expireAt) {
        this.roomId = roomId;
        this.userId = userId;
        this.expireAt = expireAt;
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getExpireAt() { return expireAt; }

    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }

    public boolean isActive() {
        return expireAt != null && expireAt.isAfter(LocalDateTime.now());
    }
}
