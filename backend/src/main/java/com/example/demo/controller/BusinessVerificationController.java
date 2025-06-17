package com.example.demo.controller;

import com.example.demo.dto.BusinessVerificationRequest;
import com.example.demo.service.BusinessVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessVerificationController {

    private final BusinessVerificationService service;

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody BusinessVerificationRequest request) {
        String bno = request.getB_no();

        // b_no가 비어있으면 400
        if (!StringUtils.hasText(bno)) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "b_no는 필수입니다.")
            );
        }

        try {
            boolean isValid = service.verifyBusiness(bno);

            // 유효하지 않은 경우 (외부 API OK지만 data가 null/비어있음)
            if (!isValid) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "b_no", bno, "valid", false, "message", "유효하지 않은 사업자등록번호입니다.")
                );
            }

            // 정상 응답
            return ResponseEntity.ok().body(
                    Map.of("success", true, "b_no", bno, "valid", true, "message", "유효한 사업자등록번호입니다.")
            );

        } catch (Exception e) {
            // 외부 API 예외 발생 시 500
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "서버 오류가 발생했습니다.", "error", e.getMessage())
            );
        }
    }
}
