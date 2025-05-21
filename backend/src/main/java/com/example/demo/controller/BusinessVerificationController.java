package com.example.demo.controller;

import com.example.demo.service.BusinessVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessVerificationController {

    private final BusinessVerificationService service;

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String bno = body.get("b_no");

        if (bno == null || bno.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "b_no는 필수입니다."));
        }

        try {
            boolean isValid = service.verifyBusiness(bno);

            return ResponseEntity.ok().body(
                    Map.of(
                            "success", true,
                            "b_no", bno,
                            "valid", isValid,
                            "message", isValid ? "유효한 사업자등록번호입니다." : "유효하지 않은 사업자등록번호입니다."
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}
