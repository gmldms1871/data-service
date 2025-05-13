package com.example.demo.client;

import com.example.demo.dto.BusinessVerificationRequest;
import com.example.demo.dto.BusinessVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class BusinessVerificationClient {

    private static final Logger logger = LoggerFactory.getLogger(BusinessVerificationClient.class);
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public BusinessVerificationClient(
            @Value("${business.api.url}") String apiUrl,
            @Value("${business.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        logger.info("BusinessVerificationClient 초기화 완료. API URL: {}", apiUrl);
        logger.debug("API Key: {}", apiKey);
    }

    public BusinessVerificationResponse verifyBusiness(BusinessVerificationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // API 키 헤더 수정 - serviceKey로 설정하고 인코딩된 키 사용
        // headers.set("Authorization", apiKey);

        // 공공데이터포털 API에 맞게 URL 구성
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/nts-businessman/v1/status")
                .queryParam("serviceKey", apiKey)
                .build()
                .toUriString();

        logger.debug("API 요청 URL: {}", url);
        logger.debug("API 요청 데이터: {}", request);

        HttpEntity<BusinessVerificationRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<BusinessVerificationResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BusinessVerificationResponse.class
            );

            logger.debug("API 응답 상태 코드: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch (Exception e) {
            logger.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}