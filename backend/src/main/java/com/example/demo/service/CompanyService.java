package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // 회원가입
    public Company register(Company company) {
        if (company.getEmail() == null || company.getPassword() == null
                || company.getUserName() == null || company.getCompanyName() == null
                || company.getBusinessNumber() == null || company.getUserPhone() == null) {
            throw new IllegalArgumentException("필수 항목이 누락되었습니다.");
        }

        if (companyRepository.findByEmail(company.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // UUID 수동 생성 불필요: 엔티티 레벨에서 자동 생성됨
        return companyRepository.save(company);
    }


    // 로그인
    public Optional<Company> login(String email, String password) {
        return companyRepository.findByEmailAndPassword(email, password);
    }

    // 내 정보 조회
    public Company findById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));
    }

    // 내 정보 업데이트
    public Company update(String id, Map<String, String> updates) {
        Company company = findById(id);

        if (updates.containsKey("userPhone")) company.setUserPhone(updates.get("userPhone"));
        if (updates.containsKey("nickname")) company.setNickname(updates.get("nickname"));
        if (updates.containsKey("companyName")) company.setCompanyName(updates.get("companyName"));
        if (updates.containsKey("homepage")) company.setHomepage(updates.get("homepage"));
        if (updates.containsKey("categoryId")) company.setCategoryId(updates.get("categoryId"));

        return companyRepository.save(company);
    }

    // 전체 기업 목록
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public void deleteById(String id) {
        Company company = findById(id);        // 존재 여부 체크
        companyRepository.delete(company);     // 엔티티 삭제
    }
}
