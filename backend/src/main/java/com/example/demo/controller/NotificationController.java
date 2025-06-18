package com.example.demo.controller;

import com.example.demo.dto.NotificationDto;
import com.example.demo.dto.NotificationRequestDto;
import com.example.demo.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** 알림 생성 - 세션에서 senderId 자동으로 가져오기 */
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @Valid @RequestBody NotificationRequestDto req,
            HttpSession session) {

        String senderId = (String) session.getAttribute("loginCompanyId");
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            NotificationDto created = notificationService.createNotification(
                    senderId,
                    req.getReceiverId(),
                    req.getTitle(),
                    req.getContent()
            );
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** 내가 받은 알림 조회 */
    @GetMapping("/received/readMany")
    public ResponseEntity<?> listReceived(HttpSession session) {
        String receiverId = (String) session.getAttribute("loginCompanyId");
        if (receiverId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            List<NotificationDto> notifications = notificationService.getNotifications(receiverId);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "알림 조회 실패", "details", e.getMessage()));
        }
    }

    /** 내가 보낸 알림 조회 */
    @GetMapping("/sent/readMany")
    public ResponseEntity<?> listSent(HttpSession session) {
        String senderId = (String) session.getAttribute("loginCompanyId");
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            List<NotificationDto> notifications = notificationService.getSentNotifications(senderId);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "알림 조회 실패", "details", e.getMessage()));
        }
    }

    /** 알림 읽음 처리 */
    @PatchMapping("/{id}/updateRead")
    public ResponseEntity<?> markAsRead(
            @PathVariable("id") String id,
            HttpSession session) {

        String userId = (String) session.getAttribute("loginCompanyId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            NotificationDto updated = notificationService.markAsRead(id, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
