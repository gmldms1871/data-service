package com.example.demo.exception.chat;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException() {
        super("채팅방을 찾을 수 없습니다.");
    }

    public ChatRoomNotFoundException(String message) {
        super(message);
    }
}