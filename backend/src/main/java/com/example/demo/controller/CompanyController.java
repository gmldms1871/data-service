package com.example.demo.controller;

import com.example.demo.dto.CompanyDto;
import com.example.demo.dto.CompanyRegistrationDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.service.CompanyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> signup(@Valid @RequestBody CompanyRegistrationDto registrationDto) {
        CompanyDto created = companyService.register(registrationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "회원가입 성공",
                        "company", created
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpSession session
    ) {
        return companyService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ).map(c -> {
            session.setAttribute("loginCompanyId", c.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "company", c
            ));
        }).orElseGet(() ->
                ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "이메일 또는 비밀번호가 일치하지 않습니다"))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(HttpSession session) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 필요"));
        }
        CompanyDto company = companyService.findById(id);
        return ResponseEntity.ok(Map.of(
                "message", "내 정보 조회 성공",
                "company", company
        ));
    }

    @PatchMapping("/users/me")
    public ResponseEntity<?> updateMyInfo(
            HttpSession session,
            @RequestBody Map<String, String> updates
    ) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 필요"));
        }
        CompanyDto updated = companyService.update(id, updates);
        return ResponseEntity.ok(Map.of(
                "message", "정보 수정 성공",
                "company", updated
        ));
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<?> deleteMyAccount(HttpSession session) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 필요"));
        }
        companyService.deleteById(id);
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴 완료"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<CompanyDto> companies = companyService.findAll();
        return ResponseEntity.ok(Map.of(
                "message", "전체 회원 조회 성공",
                "companies", companies
        ));
    }
}
