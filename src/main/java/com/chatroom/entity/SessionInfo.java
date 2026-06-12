package com.chatroom.entity;

import java.time.LocalDateTime;

public class SessionInfo {

    private String token;
    private String userId;
    private String username;
    private String role;
    private LocalDateTime loginTime;
    private LocalDateTime expireAt;

    public SessionInfo() {
    }

    public SessionInfo(String token, String userId, String username, String role, LocalDateTime loginTime, LocalDateTime expireAt) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.loginTime = loginTime;
        this.expireAt = expireAt;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public LocalDateTime getLoginTime() { return loginTime; }

    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getExpireAt() { return expireAt; }

    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isExpired() {
        return expireAt != null && expireAt.isBefore(LocalDateTime.now());
    }
}
