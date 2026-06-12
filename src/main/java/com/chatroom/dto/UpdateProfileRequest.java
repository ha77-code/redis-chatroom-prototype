package com.chatroom.dto;

public class UpdateProfileRequest {

    private String nickname;
    private String password;

    public UpdateProfileRequest() {
    }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
