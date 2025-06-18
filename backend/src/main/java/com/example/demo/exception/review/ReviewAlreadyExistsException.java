package com.example.demo.exception.review;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException() {
        super("이미 리뷰가 존재합니다.");
    }
}