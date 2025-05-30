package com.example.demo.controller;

import com.example.demo.dto.ChatRoomDto;
import com.example.demo.service.ChatRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/{transactionId}")
    public ResponseEntity<?> createChatRoom(@PathVariable String transactionId, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            ChatRoomDto room = chatRoomService.createOrGet(transactionId, loginCompanyId);
            return ResponseEntity.ok(room);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyChatRooms(HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        List<ChatRoomDto> rooms = chatRoomService.getMyChatRooms(companyId);
        return ResponseEntity.ok(Map.of("rooms", rooms));
    }

}
