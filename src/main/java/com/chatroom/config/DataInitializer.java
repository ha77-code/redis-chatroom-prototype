package com.chatroom.config;

import com.chatroom.entity.ChatRoom;
import com.chatroom.entity.User;
import com.chatroom.repository.RoomRepository;
import com.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final String defaultRoomId;
    private final String defaultRoomName;

    public DataInitializer(RoomRepository roomRepository, UserRepository userRepository,
                           @Value("${chatroom.default-room-id:room_default}") String defaultRoomId,
                           @Value("${chatroom.default-room-name:公共聊天室}") String defaultRoomName) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.defaultRoomId = defaultRoomId;
        this.defaultRoomName = defaultRoomName;
    }

    @Override
    public void run(String... args) {
        if (!roomRepository.existsById(defaultRoomId)) {
            ChatRoom room = new ChatRoom(defaultRoomId, defaultRoomName);
            roomRepository.save(room);
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setNickname("Administrator");
            admin.setRole("admin");
            admin.setStatus("active");
            userRepository.save(admin);
        }
    }
}
