package com.example.demo.exception.review;

public class UnauthorizedReviewAccessException extends RuntimeException {
    public UnauthorizedReviewAccessException() {
        super("리뷰에 접근할 권한이 없습니다.");
    }
}
