package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoomDto {
    private String id;
    private String transactionId;
    private String buyerId;
    private String sellerId;
    private String name;
    private LocalDateTime createdAt;
    private int unreadCount;
}