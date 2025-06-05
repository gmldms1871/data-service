package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private String id;
    private String roomId;
    private String senderId;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;
    private String attachmentId;
    private String readById;
    private String formattedTime;

    private boolean readByOpponent;
}