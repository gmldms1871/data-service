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
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> signup(@Valid @RequestBody CompanyRegistrationDto registrationDto) {
        if (companyService.existsByBusinessNumber(registrationDto.getBusinessNumber())) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미 등록된 사업자번호입니다."));
        }

        try {
            CompanyDto created = companyService.register(registrationDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "회원가입 성공",
                            "company", created
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpSession session
    ) {
        if (loginRequest.getPassword().startsWith("$2a$") || loginRequest.getPassword().length() > 100) {
            return ResponseEntity.badRequest().body(Map.of("error", "비정상적인 비밀번호 입력입니다."));
        }

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

    @GetMapping("/readOne")
    public ResponseEntity<?> getMyInfo(HttpSession session) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 필요"));
        }
        try {
            CompanyDto company = companyService.findActiveById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "내 정보 조회 성공",
                    "company", company
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/update")
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
        try {
            CompanyDto updated = companyService.update(id, updates);
            return ResponseEntity.ok(Map.of(
                    "message", "정보 수정 성공",
                    "company", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyAccount(HttpSession session) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 필요"));
        }
        try {
            companyService.softDelete(id);
            session.invalidate();
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴 완료"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/readMany")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<CompanyDto> companies = companyService.findAllActive();
            return ResponseEntity.ok(Map.of(
                    "message", "전체 회원 조회 성공",
                    "companies", companies
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "회원 목록 조회 실패", "details", e.getMessage()));
        }
    }
}
