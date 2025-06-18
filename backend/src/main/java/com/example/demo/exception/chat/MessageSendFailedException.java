package com.example.demo.exception.chat;

public class MessageSendFailedException extends RuntimeException {
    public MessageSendFailedException() {
        super("메시지 전송에 실패했습니다.");
    }
}
