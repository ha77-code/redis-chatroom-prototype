package com.chatroom.controller;

import com.chatroom.dto.*;
import com.chatroom.entity.Message;
import com.chatroom.entity.SessionInfo;
import com.chatroom.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatService chatService;

    public ChatRoomController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/rooms/join")
    public ApiResponse<Void> joinRoom(@RequestBody JoinRoomRequest request,
                                       HttpServletRequest httpRequest) {
        SessionInfo session = (SessionInfo) httpRequest.getAttribute("session");
        chatService.joinRoom(session.getUserId(), request.getRoomId());
        return ApiResponse.success("joined chatroom", null);
    }

    @PostMapping("/rooms/exit")
    public ApiResponse<Void> exitRoom(@RequestBody JoinRoomRequest request,
                                       HttpServletRequest httpRequest) {
        SessionInfo session = (SessionInfo) httpRequest.getAttribute("session");
        chatService.exitRoom(session.getUserId(), request.getRoomId());
        return ApiResponse.success("exited chatroom", null);
    }

    @PostMapping("/messages/send")
    public ApiResponse<Message> sendMessage(@RequestBody SendMessageRequest request,
                                            HttpServletRequest httpRequest) {
        SessionInfo session = (SessionInfo) httpRequest.getAttribute("session");
        Message message = chatService.sendMessage(session.getUserId(), session.getUsername(),
                request.getRoomId(), request.getContent());
        return ApiResponse.success(message);
    }

    @GetMapping("/messages/poll")
    public ApiResponse<List<Message>> pollMessages(@RequestParam String roomId,
                                                    @RequestParam(required = false) String lastMessageId) {
        List<Message> messages = chatService.pollMessages(roomId, lastMessageId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/rooms/online-users")
    public ApiResponse<List<OnlineUserVO>> getOnlineUsers(@RequestParam String roomId) {
        List<OnlineUserVO> users = chatService.getOnlineUsers(roomId);
        return ApiResponse.success(users);
    }

    @GetMapping("/rooms/history")
    public ApiResponse<List<Message>> getHistory(@RequestParam String roomId) {
        List<Message> messages = chatService.getHistory(roomId);
        return ApiResponse.success(messages);
    }
}
