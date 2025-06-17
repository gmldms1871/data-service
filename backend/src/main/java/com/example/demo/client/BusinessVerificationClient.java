package com.example.demo.client;

import com.example.demo.dto.BusinessVerificationRequest;
import com.example.demo.dto.BusinessVerificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "business.api")
public class BusinessVerificationClient {

    private String url;
    private String key;
    private final RestTemplate restTemplate = new RestTemplate();

    public BusinessVerificationResponse verify(String bno) {
        String fullUrl = url + "/nts-businessman/v1/status?serviceKey=" + key;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // b_no를 리스트로 감싸서 전송
        Map<String, Object> payload = Map.of("b_no", List.of(bno));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<BusinessVerificationResponse> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.POST,
                entity,
                BusinessVerificationResponse.class
        );

        return response.getBody();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
