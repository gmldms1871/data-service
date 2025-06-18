package com.example.demo.exception.chat;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException() {
        super("로그인이 필요합니다.");
    }
}