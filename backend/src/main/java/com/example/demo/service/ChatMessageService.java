package com.example.demo.service;

import com.example.demo.dto.ChatMessageDto;

import java.util.List;

public interface ChatMessageService {
    ChatMessageDto sendMessage(ChatMessageDto dto, String senderCompanyId);
    boolean hasAccessToRoom(String roomId, String companyId);
    void markMessagesAsRead(String roomId, String readerId);
    List<ChatMessageDto> getMessages(String roomId, String viewerId);
}
