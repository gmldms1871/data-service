package com.example.demo.service;

import com.example.demo.domain.Notification;
import com.example.demo.dto.NotificationDto;
import com.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // 알림 생성
    public NotificationDto createNotification(String senderId, String receiverId, String title, String content) {
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead("N");

        Notification savedNotification = notificationRepository.save(notification);
        return NotificationDto.fromEntity(savedNotification);
    }

    // 내가 받은 알림 조회
    public List<NotificationDto> getNotifications(String receiverId) {
        return notificationRepository.findByReceiverId(receiverId).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 내가 보낸 알림 조회
    public List<NotificationDto> getSentNotifications(String senderId) {
        return notificationRepository.findBySenderId(senderId).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    public NotificationDto markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다"));

        notification.setIsRead("Y");
        Notification updatedNotification = notificationRepository.save(notification);
        return NotificationDto.fromEntity(updatedNotification);
    }
}