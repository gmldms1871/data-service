package com.example.demo.repository;

import com.example.demo.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    // 엔티티 필드명 receiverId에 맞게 수정
    List<Notification> findByReceiverId(String receiverId);
}
