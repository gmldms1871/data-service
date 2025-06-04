package com.example.demo.dto;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.domain.Category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    public static ResponseEntity<?> ok(List<Category> categories) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ok'");
    }
    public static ResponseEntity<?> internalError(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'internalError'");
    }
}