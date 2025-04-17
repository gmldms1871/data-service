package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_email", nullable = false)
    private String receiverEmail;

    private String title;

    private String message;

    @Column(name = "is_read")
    private String isRead = "N";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
