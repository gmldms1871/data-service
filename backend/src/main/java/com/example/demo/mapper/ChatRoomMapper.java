package com.example.demo.mapper;

import com.example.demo.dto.ChatRoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatRoomMapper {
    ChatRoomDto findByTransaction(String transactionId);
    void insertRoom(ChatRoomDto room);
    Map<String, String> findBuyerSellerByTransactionId(String transactionId);
    List<ChatRoomDto> findRoomsByCompanyId(@Param("companyId") String companyId);

    int countUnreadMessages(@Param("roomId") String roomId, @Param("companyId") String companyId);
}
