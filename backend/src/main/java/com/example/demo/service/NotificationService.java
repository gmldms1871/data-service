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
    private final CompanyService companyService;

    public NotificationService(
            NotificationRepository notificationRepository,
            CompanyService companyService
    ) {
        this.notificationRepository = notificationRepository;
        this.companyService = companyService;
    }

    // 알림 생성
    public NotificationDto createNotification(String senderId, String receiverId, String title, String content) {

        // 🔐 수신자 존재 여부 확인
        if (!companyService.existsById(receiverId)) {
            throw new RuntimeException("존재하지 않는 수신자 ID입니다.");
        }

        // 🔐 수신자가 존재하고 탈퇴하지 않았는지 검사
        try {
            companyService.findActiveById(receiverId);
        } catch (RuntimeException e) {
            throw new RuntimeException("수신자는 존재하지 않거나 탈퇴한 계정입니다.");
        }

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

    public NotificationDto markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다"));

        // 본인 알림인지 확인
        if (!notification.getReceiverId().equals(userId)) {
            throw new RuntimeException("본인에게 온 알림만 읽을 수 있습니다");
        }

        notification.setIsRead("Y");
        Notification updatedNotification = notificationRepository.save(notification);
        return NotificationDto.fromEntity(updatedNotification);
    }

}