package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
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
@RequestMapping("/api/chatmessages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDto dto, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            log.info("📥 메시지 전송 요청: companyId={}, dto={}", companyId, dto);
            ChatMessageDto result = chatMessageService.sendMessage(dto, companyId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("❌ 메시지 처리 오류: {}", e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage() != null ? e.getMessage() : "처리 중 오류 발생"));
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getMessages(@PathVariable String roomId, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        // 채팅방 접근 권한 확인
        if (!chatMessageService.hasAccessToRoom(roomId, companyId)) {
            return ResponseEntity.status(403).body(Map.of("error", "채팅방에 접근할 수 없습니다."));
        }

        // 읽음 처리 + 메시지 조회
        chatMessageService.markMessagesAsRead(roomId, companyId);
        List<ChatMessageDto> messages = chatMessageService.getMessages(roomId, companyId);

        return ResponseEntity.ok(Map.of("messages", messages));
    }
}