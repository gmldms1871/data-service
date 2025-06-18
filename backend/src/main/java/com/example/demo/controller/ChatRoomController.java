package com.example.demo.controller;

import com.example.demo.dto.ChatRoomDto;
import com.example.demo.exception.chat.NotLoggedInException;
import com.example.demo.service.ChatRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/create/{transactionId}")
    public ResponseEntity<?> createChatRoom(@PathVariable String transactionId, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            throw new NotLoggedInException();
        }

        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("거래 ID가 유효하지 않습니다.");
        }

        ChatRoomDto room = chatRoomService.createOrGet(transactionId, loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "채팅방 생성 또는 조회 성공",
                "room", room
        ));
    }


    @GetMapping("/readMany")
    public ResponseEntity<?> getMyChatRooms(HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            throw new NotLoggedInException();
        }

        List<ChatRoomDto> rooms = chatRoomService.getMyChatRooms(companyId);
        return ResponseEntity.ok(Map.of(
                "message", "내 채팅방 목록 조회 성공",
                "rooms", rooms
        ));
    }
}
