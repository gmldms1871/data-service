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
    /** 알림 생성 */
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Map<String, String> req) {
        String receiverId = req.get("receiverId");
        String title      = req.get("title");
        String content    = req.get("content");

        Notification created = notificationService.createNotification(receiverId, title, content);
        return ResponseEntity.ok(created);
    }
    /** 알림 조회 */
    @GetMapping
    public ResponseEntity<List<Notification>> list(
            @RequestParam("receiverId") String receiverId
    ) {
        List<Notification> notifications = notificationService.getNotifications(receiverId);
        return ResponseEntity.ok(notifications);
    }
}
