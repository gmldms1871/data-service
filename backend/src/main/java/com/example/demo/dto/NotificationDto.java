package com.example.demo.dto;

import com.example.demo.domain.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private String id;
    private String receiverId;
    // senderId 필드 추가
    private String senderId;
    private String title;
    private String content;
    private String isRead;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 메서드 수정
    public static NotificationDto fromEntity(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setReceiverId(notification.getReceiverId());
        // senderId 설정 추가
        dto.setSenderId(notification.getSenderId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}