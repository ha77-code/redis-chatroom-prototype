package com.chatroom.controller;

import com.chatroom.dto.*;
import com.chatroom.service.AdminService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/mute")
    public ApiResponse<Void> muteUser(@RequestBody MuteRequest request) {
        adminService.muteUser(request.getRoomId(), request.getUserId(), request.getDurationMinutes());
        return ApiResponse.success("user muted", null);
    }

    @PostMapping("/kick")
    public ApiResponse<Void> kickUser(@RequestBody KickRequest request) {
        adminService.kickUser(request.getRoomId(), request.getUserId());
        return ApiResponse.success("user kicked", null);
    }

    @PostMapping("/messages/delete")
    public ApiResponse<Void> deleteMessage(@RequestBody DeleteMessageRequest request) {
        adminService.deleteMessage(request.getRoomId(), request.getMessageId());
        return ApiResponse.success("message deleted", null);
    }
}
