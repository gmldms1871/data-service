package com.example.demo.service;

import com.example.demo.domain.Notification;
import com.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    //생성자
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    // 알림 생성: 파라미터를 receiverId, content로 변경
    public Notification createNotification(String receiverId, String title, String content) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setTitle(title);
        notification.setContent(content);
        return notificationRepository.save(notification);
    }
    // 알림 조회: receiverId로 조회
    public List<Notification> getNotifications(String receiverId) {
        return notificationRepository.findByReceiverId(receiverId);
    }
}