package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.dto.ChatRoomDto;
import com.example.demo.mapper.ChatRoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomMapper mapper;
    private final ChatRoomMapper chatRoomMapper;

    @Override
    public ChatRoomDto createOrGet(String transactionId, String loginCompanyId) {
        log.info("[createOrGet] 채팅방 확인 요청: transactionId={}, loginCompanyId={}", transactionId, loginCompanyId);

        // 1. 기존 채팅방 있는지 확인
        ChatRoomDto room = mapper.findByTransaction(transactionId);
        if (room != null) {
            log.info("[createOrGet] 기존 채팅방 존재: roomId={}", room.getId());
            return room;
        }

        // 2. 거래에서 buyer/seller 조회
        Map<String, String> buyerSellerMap = chatRoomMapper.findBuyerSellerByTransactionId(transactionId);
        if (buyerSellerMap == null) {
            log.warn("[createOrGet] 거래 정보 없음: transactionId={}", transactionId);
            throw new IllegalArgumentException("해당 거래 정보를 찾을 수 없습니다.");
        }

        String buyerId = buyerSellerMap.get("buyerId");
        String sellerId = buyerSellerMap.get("sellerId");

        log.info("[createOrGet] 조회된 거래 참여자: buyerId={}, sellerId={}", buyerId, sellerId);

        // 3. buyer만 채팅방 생성 가능
        if (!loginCompanyId.equals(buyerId)) {
            log.warn("[createOrGet] 채팅방 생성 권한 없음: 요청자={}, buyerId={}", loginCompanyId, buyerId);
            throw new IllegalArgumentException("채팅방을 생성할 권한이 없습니다.");
        }

        // 4. 채팅방 생성
        ChatRoomDto newRoom = new ChatRoomDto();
        newRoom.setId(UUID.randomUUID().toString());
        newRoom.setTransactionId(transactionId);
        newRoom.setBuyerId(buyerId);
        newRoom.setSellerId(sellerId);
        newRoom.setName("자동 생성 채팅방");
        newRoom.setCreatedAt(LocalDateTime.now());

        mapper.insertRoom(newRoom);
        log.info("[createOrGet] 채팅방 생성 완료: roomId={}", newRoom.getId());

        return newRoom;
    }

    @Override
    public List<ChatRoomDto> getMyChatRooms(String companyId) {
        List<ChatRoomDto> rooms = mapper.findRoomsByCompanyId(companyId);
        for (ChatRoomDto room : rooms) {
            int unreadCount = mapper.countUnreadMessages(room.getId(), companyId);
            room.setUnreadCount(unreadCount);
        }
        return rooms;
    }
}

