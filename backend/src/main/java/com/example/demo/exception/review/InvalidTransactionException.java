package com.example.demo.exception.review;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException() {
        super("유효하지 않은 거래입니다.");
    }
}