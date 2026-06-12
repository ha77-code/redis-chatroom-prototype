package com.chatroom.service;

import com.chatroom.dto.OnlineUserVO;
import com.chatroom.entity.*;
import com.chatroom.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ChatService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final OnlineUserRepository onlineUserRepository;
    private final ModerationRepository moderationRepository;

    public ChatService(UserRepository userRepository, RoomRepository roomRepository,
                       MessageRepository messageRepository, OnlineUserRepository onlineUserRepository,
                       ModerationRepository moderationRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.onlineUserRepository = onlineUserRepository;
        this.moderationRepository = moderationRepository;
    }

    public void joinRoom(String userId, String roomId) {
        ChatRoom room = roomRepository.findById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("chatroom not found");
        }
        if ("closed".equals(room.getStatus())) {
            throw new IllegalStateException("chatroom is closed");
        }
        if (moderationRepository.isKicked(roomId, userId)) {
            throw new IllegalStateException("you have been kicked from this chatroom");
        }

        onlineUserRepository.addOnlineUser(userId, roomId);
    }

    public void exitRoom(String userId, String roomId) {
        onlineUserRepository.removeOnlineUser(userId);
    }

    public Message sendMessage(String userId, String username, String roomId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("message content is required");
        }
        if (!onlineUserRepository.isOnline(userId)) {
            throw new IllegalStateException("you are not in the chatroom, please join first");
        }

        String userRoomId = onlineUserRepository.getUserRoomId(userId);
        if (!roomId.equals(userRoomId)) {
            throw new IllegalStateException("you are not in this chatroom");
        }

        if (moderationRepository.isMuted(roomId, userId)) {
            MuteRecord mute = moderationRepository.getMuteRecord(roomId, userId);
            throw new IllegalStateException("you are muted until " + mute.getExpireAt());
        }

        Message message = new Message();
        message.setMessageId("msg_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        message.setRoomId(roomId);
        message.setSenderId(userId);
        message.setSenderName(username);
        message.setContent(content);

        messageRepository.save(message);

        onlineUserRepository.updateLastActiveTime(userId);
        return message;
    }

    public List<Message> pollMessages(String roomId, String lastMessageId) {
        List<Message> messages = messageRepository.findMessagesAfter(roomId, lastMessageId);
        List<Message> visible = new ArrayList<>();
        for (Message msg : messages) {
            if (!msg.isDeleted()) {
                visible.add(msg);
            }
        }
        return visible;
    }

    public List<OnlineUserVO> getOnlineUsers(String roomId) {
        Set<String> userIds = onlineUserRepository.getOnlineUserIds();
        List<OnlineUserVO> result = new ArrayList<>();
        for (String uid : userIds) {
            String urid = onlineUserRepository.getUserRoomId(uid);
            if (roomId.equals(urid)) {
                User user = userRepository.findById(uid);
                if (user != null) {
                    result.add(OnlineUserVO.fromUser(user, urid));
                }
            }
        }
        return result;
    }

    public List<Message> getHistory(String roomId) {
        List<Message> messages = messageRepository.findMessagesAfter(roomId, null);
        List<Message> visible = new ArrayList<>();
        for (Message msg : messages) {
            if (!msg.isDeleted()) {
                visible.add(msg);
            }
        }
        return visible;
    }
}
