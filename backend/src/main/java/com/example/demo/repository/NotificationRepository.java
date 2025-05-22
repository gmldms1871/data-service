package com.example.demo.repository;

import com.example.demo.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByReceiverId(String receiverId);
    List<Notification> findBySenderId(String senderId);  // 발신자 ID로 조회 추가
}