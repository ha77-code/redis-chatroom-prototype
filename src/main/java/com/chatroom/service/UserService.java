package com.chatroom.service;

import com.chatroom.dto.UpdateProfileRequest;
import com.chatroom.entity.User;
import com.chatroom.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }
        return user;
    }

    public User updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 6) {
                throw new IllegalArgumentException("password must be at least 6 characters");
            }
            user.setPassword(request.getPassword());
        }

        userRepository.update(user);
        return user;
    }
}
