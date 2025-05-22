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

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** 알림 생성 - 세션에서 senderId 자동으로 가져오기 */
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody NotificationRequestDto req,
            HttpSession session) {

        // 세션에서 로그인한 사용자 ID 가져오기
        String senderId = (String) session.getAttribute("loginCompanyId");
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "로그인이 필요합니다"));
        }

        NotificationDto created = notificationService.createNotification(
                senderId,              // 세션에서 가져온 발신자 ID
                req.getReceiverId(),   // 수신자 ID
                req.getTitle(),        // 제목
                req.getContent()       // 내용
        );

        return ResponseEntity.ok(created);
    }

    /** 내가 받은 알림 조회 */
    @GetMapping("/received")
    public ResponseEntity<?> listReceived(HttpSession session) {
        String receiverId = (String) session.getAttribute("loginCompanyId");
        if (receiverId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "로그인이 필요합니다"));
        }

        List<NotificationDto> notifications = notificationService.getNotifications(receiverId);
        return ResponseEntity.ok(notifications);
    }

    /** 내가 보낸 알림 조회 */
    @GetMapping("/sent")
    public ResponseEntity<?> listSent(HttpSession session) {
        String senderId = (String) session.getAttribute("loginCompanyId");
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "로그인이 필요합니다"));
        }

        List<NotificationDto> notifications = notificationService.getSentNotifications(senderId);
        return ResponseEntity.ok(notifications);
    }

    /** 알림 읽음 처리 */
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable("id") String id,
            HttpSession session) {

        String userId = (String) session.getAttribute("loginCompanyId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "로그인이 필요합니다"));
        }

        NotificationDto updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(updated);
    }
}