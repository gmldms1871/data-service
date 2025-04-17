package com.example.demo.controller;

import com.example.demo.domain.Company;
import com.example.demo.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SessionAttributes("loginCompanyId")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> signup(@RequestBody Company company) {
        try {
            Company created = companyService.register(company);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        Optional<Company> company = companyService.login(
                request.get("email"),
                request.get("password")
        );

        if (company.isPresent()) {
            session.setAttribute("loginCompanyId", company.get().getId());
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(HttpSession session) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");

        Company company = companyService.findById(id);
        return ResponseEntity.ok(company);
    }
    @PatchMapping("/users/me")
    public ResponseEntity<?> updateMyInfo(HttpSession session, @RequestBody Map<String, String> updates) {
        String id = (String) session.getAttribute("loginCompanyId");
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");

        Company updated = companyService.update(id, updates);
        return ResponseEntity.ok(updated);
    }
}
