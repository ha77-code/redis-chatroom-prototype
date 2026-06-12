package com.chatroom.entity;

import java.time.LocalDateTime;

public class Message {

    private String messageId;
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private LocalDateTime sendTime;
    private boolean deleted;

    public Message() {
    }

    public Message(String messageId, String roomId, String senderId, String senderName, String content) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.sendTime = LocalDateTime.now();
        this.deleted = false;
    }

    public String getMessageId() { return messageId; }

    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getSenderId() { return senderId; }

    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }

    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSendTime() { return sendTime; }

    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
