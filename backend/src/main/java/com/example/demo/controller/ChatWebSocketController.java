package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
import com.example.demo.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send") // /app/chat.send 로 클라이언트가 전송
    public void send(ChatMessageDto message, Principal principal) {
        String senderId = principal.getName(); // 세션 인증된 사용자 ID
        ChatMessageDto saved = chatMessageService.sendMessage(message, senderId);

        // 구독자들에게 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/" + saved.getRoomId(), saved);
    }
}
