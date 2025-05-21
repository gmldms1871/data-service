package com.example.demo.service;

import com.example.demo.client.BusinessVerificationClient;
import com.example.demo.dto.BusinessVerificationRequest;
import com.example.demo.dto.BusinessVerificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessVerificationService {

    private final BusinessVerificationClient client;

    public boolean verifyBusiness(String bno) {
        BusinessVerificationRequest request = new BusinessVerificationRequest(List.of(bno));
        BusinessVerificationResponse response = client.verify(request);

        if (!"OK".equals(response.getStatus_code()) || response.getData().isEmpty()) return false;

        String taxType = response.getData().get(0).getTax_type();
        return taxType == null || !taxType.contains("등록되지 않은");
    }
}
