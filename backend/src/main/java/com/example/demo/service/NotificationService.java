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
    //알림 생성(문의 생성할 떄 동시 호출 추가해야함)
    public Notification createNotification(String receiverEmail, String title, String message) {
        Notification notification = new Notification();
        notification.setReceiverEmail(receiverEmail);
        notification.setTitle(title);
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }
    //알림 조회
    public List<Notification> getNotifications(String receiverEmail) {
        return notificationRepository.findByReceiverEmail(receiverEmail);
    }
}