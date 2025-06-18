package com.example.demo.exception;

import com.example.demo.exception.chat.InvalidChatRoomAccessException;
import com.example.demo.exception.chat.NotLoggedInException;
import com.example.demo.exception.chat.MessageSendFailedException;
import com.example.demo.exception.chat.ChatRoomNotFoundException;
import com.example.demo.exception.chat.ChatRoomCreationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ChatExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatExceptionHandler.class);

    @ExceptionHandler(NotLoggedInException.class)
    public ResponseEntity<?> handleNotLoggedIn(NotLoggedInException ex) {
        log.warn("로그인 필요: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(InvalidChatRoomAccessException.class)
    public ResponseEntity<?> handleInvalidAccess(InvalidChatRoomAccessException ex) {
        log.warn("채팅방 접근 거부: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<?> handleRoomNotFound(ChatRoomNotFoundException ex) {
        log.warn("채팅방 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(ChatRoomCreationException.class)
    public ResponseEntity<?> handleRoomCreationFailure(ChatRoomCreationException ex) {
        log.error("채팅방 생성 실패: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(MessageSendFailedException.class)
    public ResponseEntity<?> handleSendFailure(MessageSendFailedException ex) {
        log.warn("메시지 전송 실패: {}", ex.getMessage());
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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        log.error("잘못된 상태 오류: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("잘못된 요청 파라미터: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", ex.getMessage()
        ));
    }
}
