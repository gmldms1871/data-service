package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(
            name = "uuid2",
            strategy = "org.hibernate.id.UUIDGenerator"  // uuid2 대신 클래스명 명시
    )
    @Column(name = "id",
            updatable = false,
            nullable = false,
            length = 100)               // DB 스키마와 동일하게 100
    private String id;

    // receiver_email → receiver_id 로 변경
    @Column(name = "receiver_id", nullable = false, length = 100)
    private String receiverId;

    @Column(nullable = false)
    private String title;

    // message → content 로 변경
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
