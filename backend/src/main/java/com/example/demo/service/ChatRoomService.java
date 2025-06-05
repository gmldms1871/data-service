package com.example.demo.service;

import com.example.demo.dto.ChatRoomDto;

import java.util.List;

public interface ChatRoomService {
    ChatRoomDto createOrGet(String transactionId, String loginCompanyId);
    List<ChatRoomDto> getMyChatRooms(String companyId);
}