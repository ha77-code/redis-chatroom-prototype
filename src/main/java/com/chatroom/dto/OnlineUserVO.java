package com.chatroom.dto;

import com.chatroom.entity.User;

public class OnlineUserVO {

    private String userId;
    private String username;
    private String nickname;
    private String role;
    private String roomId;

    public OnlineUserVO() {
    }

    public static OnlineUserVO fromUser(User user, String roomId) {
        OnlineUserVO vo = new OnlineUserVO();
        vo.userId = user.getUserId();
        vo.username = user.getUsername();
        vo.nickname = user.getNickname();
        vo.role = user.getRole();
        vo.roomId = roomId;
        return vo;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
}
