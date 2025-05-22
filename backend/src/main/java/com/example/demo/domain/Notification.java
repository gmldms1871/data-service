package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id",
            updatable = false,
            nullable = false,
            length = 100)
    private String id;

    @Column(name = "receiver_id", nullable = false, length = 100)
    private String receiverId;

    // 발신자 ID 필드 - 데이터베이스 컬럼명과 일치
    @Column(name = "sender_id", length = 100)
    private String senderId;

    @Column(nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false, length = 1)
    private String isRead = "N";

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}