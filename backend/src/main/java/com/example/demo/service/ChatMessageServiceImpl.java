package com.example.demo.service;

import com.example.demo.dto.ChatMessageDto;
import com.example.demo.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageMapper mapper;

    @Override
    public ChatMessageDto sendMessage(ChatMessageDto dto, String senderCompanyId) {
        // 권한 체크: 채팅방 참여자인지 확인
        int access = mapper.countRoomAccess(dto.getRoomId(), senderCompanyId);
        if (access == 0) throw new IllegalArgumentException("채팅방에 참여할 수 없습니다");

        // 메시지 생성
        dto.setId(UUID.randomUUID().toString());
        dto.setSenderId(senderCompanyId);
        dto.setSentAt(LocalDateTime.now());
        dto.setRead(false);
        if (dto.getAttachmentId() != null && dto.getAttachmentId().isBlank()) {
            dto.setAttachmentId(null);
        }

        mapper.insertMessage(dto);

        dto.setFormattedTime(formatTime(dto.getSentAt()));

        return dto;
    }

    @Override
    public boolean hasAccessToRoom(String roomId, String companyId) {
        return mapper.countRoomAccess(roomId, companyId) > 0;
    }

    // 3. 메시지 읽음 처리
    @Override
    public void markMessagesAsRead(String roomId, String readerId) {
        mapper.updateReadMessages(roomId, readerId);
    }

    // 4. 메시지 조회
    @Override
    public List<ChatMessageDto> getMessages(String roomId, String viewerId) {
        List<ChatMessageDto> messages = mapper.findMessagesByRoomId(roomId);

        for (ChatMessageDto msg : messages) {
            msg.setFormattedTime(formatTime(msg.getSentAt()));

            if (msg.getSenderId() != null && msg.getSenderId().equals(viewerId)) {
                // 내가 보낸 메시지 → 상대방이 읽었는지 체크
                msg.setReadByOpponent(msg.getReadById() != null);
            } else {
                // 내가 받은 메시지
                msg.setReadByOpponent(false); // 의미 없음
            }
        }

        messages.forEach(msg -> msg.setFormattedTime(formatTime(msg.getSentAt())));
        return messages;
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "알 수 없음";
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(time, now);
        long hours = ChronoUnit.HOURS.between(time, now);
        long days = ChronoUnit.DAYS.between(time.toLocalDate(), now.toLocalDate());

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (hours < 24) return hours + "시간 전";
        if (days == 1) return "어제";
        if (days < 7) return days + "일 전";

        // 그 외는 날짜로
        return time.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

}