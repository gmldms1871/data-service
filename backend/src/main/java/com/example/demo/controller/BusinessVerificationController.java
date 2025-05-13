package com.example.demo.controller;

import com.example.demo.service.BusinessVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/business")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4601"}, allowCredentials = "true")
public class BusinessVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessVerificationController.class);
    private final BusinessVerificationService verificationService;

    public BusinessVerificationController(BusinessVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyBusiness(@RequestBody Map<String, String> request) {
        logger.info("사업자등록번호 진위확인 요청: {}", request);

        String businessNumber = request.get("businessNumber");
        String companyName = request.get("companyName");
        String representativeName = request.get("representativeName");
        String openDate = request.get("openDate");

        // 필수 파라미터 체크
        if (businessNumber == null || businessNumber.trim().isEmpty()) {
            logger.warn("사업자등록번호가 누락되었습니다.");
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "사업자등록번호는 필수입니다."
            ));
        }

        // 하이픈(-) 제거
        businessNumber = businessNumber.replaceAll("-", "");

        // 사업자번호 길이 검증
        if (businessNumber.length() != 10) {
            logger.warn("사업자등록번호 형식이 잘못되었습니다: {}", businessNumber);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "사업자등록번호는 10자리 숫자여야 합니다."
            ));
        }

        try {
            boolean isValid = verificationService.verifyBusiness(
                    businessNumber,
                    companyName,
                    representativeName,
                    openDate
            );

            logger.info("사업자등록번호 진위확인 결과: {}", isValid ? "유효" : "유효하지 않음");

            Map<String, Object> response = Map.of(
                    "businessNumber", businessNumber,
                    "valid", isValid,
                    "success", true,
                    "message", isValid ? "유효한 사업자등록번호입니다." : "유효하지 않은 사업자등록번호입니다."
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("사업자등록번호 진위확인 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "진위확인 처리 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}