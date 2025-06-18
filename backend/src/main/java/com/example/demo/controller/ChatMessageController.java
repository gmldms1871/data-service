package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
import com.example.demo.exception.chat.InvalidChatRoomAccessException;
import com.example.demo.exception.chat.MessageSendFailedException;
import com.example.demo.exception.chat.NotLoggedInException;
import com.example.demo.service.ChatMessageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chatmessage")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping("/create")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDto dto, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            throw new NotLoggedInException();
        }

        if (dto.getRoomId() == null || dto.getRoomId().isBlank()) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }

        log.info("메시지 전송 요청: companyId={}, dto={}", companyId, dto);

        ChatMessageDto result = chatMessageService.sendMessage(dto, companyId);

        return ResponseEntity.ok(Map.of(
                "message", "메시지 전송 성공",
                "messageData", result
        ));
    }


    @GetMapping("/readMany/{roomId}")
    public ResponseEntity<?> getMessages(@PathVariable String roomId, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            throw new NotLoggedInException();
        }

        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }

        if (!chatMessageService.hasAccessToRoom(roomId, companyId)) {
            throw new InvalidChatRoomAccessException();
        }

        chatMessageService.markMessagesAsRead(roomId, companyId);
        List<ChatMessageDto> messages = chatMessageService.getMessages(roomId, companyId);

        return ResponseEntity.ok(Map.of(
                "message", "채팅 메시지 조회 성공",
                "messages", messages
        ));
    }
}
