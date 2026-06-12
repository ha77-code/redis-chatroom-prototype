package com.chatroom.service;

import com.chatroom.dto.LoginRequest;
import com.chatroom.dto.LoginResponse;
import com.chatroom.dto.RegisterRequest;
import com.chatroom.entity.SessionInfo;
import com.chatroom.entity.User;
import com.chatroom.repository.SessionRepository;
import com.chatroom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public LoginResponse register(RegisterRequest request) {
        validateRegister(request);
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole("user");
        user.setStatus("active");
        user.setCreateTime(LocalDateTime.now());

        user = userRepository.save(user);
        return createSession(user);
    }

    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required");
        }

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("invalid username or password");
        }
        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("invalid username or password");
        }
        if ("disabled".equals(user.getStatus())) {
            throw new IllegalStateException("account is disabled");
        }

        return createSession(user);
    }

    public void logout(String token) {
        sessionRepository.deleteByToken(token);
    }

    public SessionInfo validateSession(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("not logged in");
        }
        SessionInfo session = sessionRepository.findByToken(token);
        if (session == null || session.isExpired()) {
            throw new IllegalStateException("session expired, please login again");
        }
        sessionRepository.refreshExpiry(token);
        return session;
    }

    private LoginResponse createSession(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        SessionInfo session = new SessionInfo(token, user.getUserId(), user.getUsername(),
                user.getRole(), now, now.plusMinutes(30));
        sessionRepository.save(session);

        return new LoginResponse(token, user.getUserId(), user.getUsername(), user.getRole());
    }

    private void validateRegister(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            throw new IllegalArgumentException("username must be 3-20 characters");
        }
        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("password must be at least 6 characters");
        }
    }
}
