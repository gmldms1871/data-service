package com.example.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResponse {
    private String status_code;
    private int request_cnt;
    private List<BusinessResult> data;
}
