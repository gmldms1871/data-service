package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResponse {
    private String request_cnt;
    private String status_code;
    private String status_msg;
    private List<BusinessResult> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessResult {
        private String b_no; // 사업자번호
        private String valid; // 검증결과 (01: 계속사업자, 02: 휴업자, 03: 폐업자)
        private String valid_msg; // 검증결과 메시지
    }
}