package com.chatroom.controller;

import com.chatroom.dto.ApiResponse;
import com.chatroom.dto.UpdateProfileRequest;
import com.chatroom.entity.SessionInfo;
import com.chatroom.entity.User;
import com.chatroom.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/profile/update")
    public ApiResponse<User> updateProfile(@RequestBody UpdateProfileRequest request,
                                           HttpServletRequest httpRequest) {
        SessionInfo session = (SessionInfo) httpRequest.getAttribute("session");
        User user = userService.updateProfile(session.getUserId(), request);
        user.setPassword(null); // never return password
        return ApiResponse.success(user);
    }
}
