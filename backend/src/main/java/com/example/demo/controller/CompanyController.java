package com.example.demo.controller;

import com.example.demo.domain.Company;
import com.example.demo.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PATCH,
                RequestMethod.DELETE
        }
)
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> signup(@RequestBody Company company) {
        try {
            Company created = companyService.register(company);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "회원가입 성공",
                            "company", created
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpSession session
    ) {
        return companyService.login(
                request.get("email"),
                request.get("password")
        ).map(c -> {
            session.setAttribute("loginCompanyId", c.getId());
            // 세션이 생성되면서 Set-Cookie: JSESSIONID 헤더가 자동으로 붙습니다.
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
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();  // 세션 무효화 → 클라이언트의 JSESSIONID 쿠키도 무효화됨
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
        Company company = companyService.findById(id);
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
        Company updated = companyService.update(id, updates);
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
        List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(Map.of(
                "message", "전체 회원 조회 성공",
                "companies", companies
        ));
    }
}
