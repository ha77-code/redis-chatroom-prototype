package com.chatroom.dto;

public class SendMessageRequest {

    private String roomId;
    private String content;

    public SendMessageRequest() {
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }
}
