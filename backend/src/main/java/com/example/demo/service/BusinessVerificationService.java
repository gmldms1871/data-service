package com.example.demo.service;

import com.example.demo.client.BusinessVerificationClient;
import com.example.demo.dto.BusinessVerificationResponse;
import com.example.demo.dto.BusinessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessVerificationService {

    private final BusinessVerificationClient client;

    public boolean verifyBusiness(String bno) {
        try {
            BusinessVerificationResponse response = client.verify(bno);

            if (response == null || !"OK".equalsIgnoreCase(response.getStatusCode())) {
                throw new RuntimeException("외부 API 상태 코드가 OK 아님");
            }

            List<BusinessResult> results = response.getData();
            if (results == null || results.isEmpty()) {
                return false; // 이건 진짜 유효하지 않은 번호일 때
            }

            String taxType = results.get(0).getTaxType();
            return taxType != null && !taxType.contains("등록되지 않은");

        } catch (Exception e) {
            // 외부 API 오류는 무조건 예외로 던짐
            System.out.println("외부 API 통신 실패: " + e.getMessage());
            throw new RuntimeException("외부 API 오류 발생", e);
        }
    }
}
