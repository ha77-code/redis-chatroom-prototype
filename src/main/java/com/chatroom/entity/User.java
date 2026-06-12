package com.chatroom.entity;

import java.time.LocalDateTime;

public class User {

    private String userId;
    private String username;
    private String password;
    private String nickname;
    private String role;     // "user" or "admin"
    private String status;   // "active" or "disabled"
    private LocalDateTime createTime;

    public User() {
    }

    public User(String userId, String username, String password, String nickname, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.status = "active";
        this.createTime = LocalDateTime.now();
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }

    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
