package com.example.demo.mapper;

import com.example.demo.dto.ChatMessageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    void insertMessage(ChatMessageDto message);

    int countRoomAccess(@Param("roomId") String roomId, @Param("companyId") String companyId);

    void updateReadMessages(@Param("roomId") String roomId, @Param("readerId") String readerId);

    List<ChatMessageDto> findMessagesByRoomId(@Param("roomId") String roomId);
}