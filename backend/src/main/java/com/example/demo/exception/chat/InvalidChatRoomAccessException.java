package com.example.demo.exception.chat;

public class InvalidChatRoomAccessException extends RuntimeException {
    public InvalidChatRoomAccessException() {
        super("채팅방에 접근할 수 없습니다.");
    }
}