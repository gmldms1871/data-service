package com.example.demo.exception;

import com.example.demo.exception.review.InvalidTransactionException;
import com.example.demo.exception.review.ReviewAlreadyExistsException;
import com.example.demo.exception.review.ReviewNotFoundException;
import com.example.demo.exception.review.UnauthorizedReviewAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ReviewExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ReviewExceptionHandler.class);

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<?> handleReviewNotFound(ReviewNotFoundException ex) {
        log.warn("리뷰 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<?> handleDuplicateReview(ReviewAlreadyExistsException ex) {
        log.warn("리뷰 중복: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(UnauthorizedReviewAccessException.class)
    public ResponseEntity<?> handleAccessDenied(UnauthorizedReviewAccessException ex) {
        log.warn("리뷰 권한 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<?> handleInvalidTransaction(InvalidTransactionException ex) {
        log.warn("유효하지 않은 거래: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        log.error("알 수 없는 오류 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "서버 내부 오류가 발생했습니다.",
                "detail", ex.getMessage()
        ));
    }
}

