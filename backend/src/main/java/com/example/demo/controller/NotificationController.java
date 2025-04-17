package com.example.demo.controller;

import com.example.demo.domain.Notification;
import com.example.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    //생성자
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    //알림 생성
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> request) {
        String receiverEmail = request.get("receiverEmail");
        String title = request.get("title");
        String message = request.get("message");

        Notification created = notificationService.createNotification(receiverEmail, title, message);
        return ResponseEntity.ok(created);
    }
    //알림 조회
    @GetMapping
    public ResponseEntity<?> get(@RequestParam("userId") String email) {
        List<Notification> notifications = notificationService.getNotifications(email);
        return ResponseEntity.ok(notifications);
    }
}
