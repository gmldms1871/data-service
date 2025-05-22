package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.dto.CompanyDto;
import com.example.demo.dto.CompanyRegistrationDto;
import com.example.demo.repository.CompanyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public CompanyDto register(CompanyRegistrationDto registrationDto) {
        if (companyRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        Company company = new Company();
        // 기본 정보 매핑
        mapToCompany(registrationDto, company);
        // 비밀번호는 암호화가 필요하므로 별도 처리
        company.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        Company savedCompany = companyRepository.save(company);
        return CompanyDto.fromEntity(savedCompany);
    }

    // 로그인
    public Optional<CompanyDto> login(String email, String password) {
        Optional<Company> companyOpt = companyRepository.findByEmail(email);

        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            if (passwordEncoder.matches(password, company.getPassword())) {
                return Optional.of(CompanyDto.fromEntity(company));
            }
        }

        return Optional.empty();
    }

    // 내 정보 조회
    public CompanyDto findById(String id) {
        Company company = getCompanyById(id);
        return CompanyDto.fromEntity(company);
    }

    // 내 정보 업데이트
    public CompanyDto update(String id, Map<String, String> updates) {
        Company company = getCompanyById(id);

        // 업데이트 정보 적용
        applyUpdates(company, updates);

        // 비밀번호 변경 요청이 있는 경우 (암호화 필요)
        if (updates.containsKey("password")) {
            company.setPassword(passwordEncoder.encode(updates.get("password")));
        }

        Company updatedCompany = companyRepository.save(company);
        return CompanyDto.fromEntity(updatedCompany);
    }

    // 전체 기업 목록
    public List<CompanyDto> findAll() {
        return companyRepository.findAll().stream()
                .map(CompanyDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 회원 삭제
    public void deleteById(String id) {
        Company company = getCompanyById(id);
        companyRepository.delete(company);
    }

    // =============== 헬퍼 메서드 ===============

    /**
     * ID로 Company 엔티티를 조회하는 헬퍼 메서드
     */
    private Company getCompanyById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));
    }

    /**
     * DTO에서 Company 엔티티로 데이터를 매핑하는 헬퍼 메서드
     */
    private void mapToCompany(CompanyRegistrationDto dto, Company company) {
        company.setCategoryId(dto.getCategoryId());
        company.setEmail(dto.getEmail());
        company.setUserName(dto.getUserName());
        company.setUserPhone(dto.getUserPhone());
        company.setNickname(dto.getNickname());
        company.setCompanyName(dto.getCompanyName());
        company.setProfileAttachmentUrl(dto.getProfileAttachmentUrl());
        company.setBusinessNumber(dto.getBusinessNumber());
        company.setHomepage(dto.getHomepage());
    }

    /**
     * 업데이트 정보를 Company 엔티티에 적용하는 헬퍼 메서드
     */
    private void applyUpdates(Company company, Map<String, String> updates) {
        if (updates.containsKey("userPhone")) company.setUserPhone(updates.get("userPhone"));
        if (updates.containsKey("nickname")) company.setNickname(updates.get("nickname"));
        if (updates.containsKey("companyName")) company.setCompanyName(updates.get("companyName"));
        if (updates.containsKey("homepage")) company.setHomepage(updates.get("homepage"));
        if (updates.containsKey("categoryId")) company.setCategoryId(updates.get("categoryId"));
        // 비밀번호는 암호화가 필요하므로 이 메서드에서 처리하지 않음
    }
}