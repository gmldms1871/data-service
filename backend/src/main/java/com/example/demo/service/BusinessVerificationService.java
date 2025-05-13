package com.example.demo.service;

import com.example.demo.client.BusinessVerificationClient;
import com.example.demo.dto.BusinessVerificationRequest;
import com.example.demo.dto.BusinessVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Collections;

@Service
public class BusinessVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessVerificationService.class);
    private final BusinessVerificationClient verificationClient;

    public BusinessVerificationService(BusinessVerificationClient verificationClient) {
        this.verificationClient = verificationClient;
    }

    /**
     * 사업자등록번호 진위확인
     * @param businessNumber 사업자등록번호 (하이픈 없는 10자리)
     * @param companyName 회사명
     * @param representativeName 대표자명
     * @param openDate 개업일자 (YYYYMMDD 형식)
     * @return 유효 여부 (true: 유효, false: 유효하지 않음)
     */
    public boolean verifyBusiness(String businessNumber, String companyName, String representativeName, String openDate) {
        try {
            logger.info("사업자등록번호 진위확인 시작: {}", businessNumber);

            // 요청 객체 생성
            BusinessVerificationRequest.BusinessInfo info = new BusinessVerificationRequest.BusinessInfo();
            info.setB_no(businessNumber);
            info.setB_nm(companyName);
            info.setP_nm(representativeName);
            info.setStart_dt(openDate);

            BusinessVerificationRequest request = new BusinessVerificationRequest();
            request.setBusinesses(Collections.singletonList(info));

            // API 호출
            BusinessVerificationResponse response = verificationClient.verifyBusiness(request);
            logger.debug("API 응답: {}", response);

            // 응답 검증
            if (response.getStatus_code() == null || !"00".equals(response.getStatus_code())) {
                logger.warn("API 호출 결과 오류: {}", response.getStatus_msg());
                return false;
            }

            if (response.getData() == null || response.getData().isEmpty()) {
                logger.warn("사업자 정보가 없습니다");
                return false;
            }

            // 사업자 상태 확인 (01: 계속사업자, 02: 휴업자)
            String validCode = response.getData().get(0).getValid();
            boolean isValid = "01".equals(validCode) || "02".equals(validCode);

            logger.info("사업자등록번호 진위확인 결과: {}, 상태코드: {}", isValid, validCode);
            return isValid;
        } catch (RestClientException e) {
            logger.error("API 호출 중 오류 발생", e);
            throw new RuntimeException("사업자등록번호 진위확인 API 호출 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("진위확인 처리 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("사업자등록번호 진위확인 처리 중 오류가 발생했습니다.", e);
        }
    }
}