package com.chatroom.dto;

public class DeleteMessageRequest {

    private String roomId;
    private String messageId;

    public DeleteMessageRequest() {
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getMessageId() { return messageId; }

    public void setMessageId(String messageId) { this.messageId = messageId; }
}
