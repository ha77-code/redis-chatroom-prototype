package com.chatroom.service;

import com.chatroom.repository.MessageRepository;
import com.chatroom.repository.ModerationRepository;
import com.chatroom.repository.OnlineUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final ModerationRepository moderationRepository;
    private final MessageRepository messageRepository;
    private final OnlineUserRepository onlineUserRepository;

    public AdminService(ModerationRepository moderationRepository,
                        MessageRepository messageRepository,
                        OnlineUserRepository onlineUserRepository) {
        this.moderationRepository = moderationRepository;
        this.messageRepository = messageRepository;
        this.onlineUserRepository = onlineUserRepository;
    }

    public void muteUser(String roomId, String userId, int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        moderationRepository.muteUser(roomId, userId, durationMinutes);
    }

    public void kickUser(String roomId, String userId) {
        moderationRepository.kickUser(roomId, userId);
        onlineUserRepository.removeOnlineUser(userId);
    }

    public void deleteMessage(String roomId, String messageId) {
        boolean found = messageRepository.markDeleted(roomId, messageId);
        if (!found) {
            throw new IllegalArgumentException("message not found");
        }
    }
}
