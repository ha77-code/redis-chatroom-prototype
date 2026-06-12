package com.chatroom.controller;

import com.chatroom.dto.ApiResponse;
import com.chatroom.dto.LoginRequest;
import com.chatroom.dto.LoginResponse;
import com.chatroom.dto.RegisterRequest;
import com.chatroom.entity.SessionInfo;
import com.chatroom.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        SessionInfo session = (SessionInfo) request.getAttribute("session");
        authService.logout(session.getToken());
        return ApiResponse.success(null);
    }
}
