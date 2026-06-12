package com.chatroom.dto;

public class MuteRequest {

    private String roomId;
    private String userId;
    private int durationMinutes;

    public MuteRequest() {
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public int getDurationMinutes() { return durationMinutes; }

    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
