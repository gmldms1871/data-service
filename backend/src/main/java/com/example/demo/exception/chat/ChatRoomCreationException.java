package com.example.demo.exception.chat;

public class ChatRoomCreationException extends RuntimeException {
    public ChatRoomCreationException() {
        super("채팅방 생성 중 오류가 발생했습니다.");
    }

    public ChatRoomCreationException(String message) {
        super(message);
    }
}
