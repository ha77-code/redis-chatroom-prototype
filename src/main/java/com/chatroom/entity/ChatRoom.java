package com.chatroom.entity;

import java.time.LocalDateTime;

public class ChatRoom {

    private String roomId;
    private String roomName;
    private String status;    // "open" or "closed"
    private String notice;
    private LocalDateTime createTime;

    public ChatRoom() {
    }

    public ChatRoom(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.status = "open";
        this.notice = "";
        this.createTime = LocalDateTime.now();
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }

    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getNotice() { return notice; }

    public void setNotice(String notice) { this.notice = notice; }

    public LocalDateTime getCreateTime() { return createTime; }

    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
