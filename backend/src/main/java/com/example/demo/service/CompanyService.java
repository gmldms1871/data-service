package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.Notification;
import com.example.demo.dto.CompanyDto;
import com.example.demo.dto.CompanyRegistrationDto;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.NotificationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            NotificationRepository notificationRepository, // ✅ 주입
            PasswordEncoder passwordEncoder
    ) {
        this.companyRepository = companyRepository;
        this.notificationRepository = notificationRepository;
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

            // ✅ 삭제된 사용자 차단
            if (company.getDeletedAt() != null) {
                return Optional.empty();
            }

            // 암호화된 비밀번호 비교
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

    public void softDelete(String id) {
        Company company = getCompanyById(id);

        // 개인정보 마스킹
        company.setEmail("deleted_" + UUID.randomUUID() + "@example.com");
        company.setUserName("탈퇴한 사용자");
        company.setUserPhone("");
        company.setNickname(null);
        company.setCompanyName("삭제된 기업");
        company.setBusinessNumber(null);
        company.setHomepage(null);
        company.setProfileAttachmentUrl(null);

        // 삭제 시간 기록
        company.setDeletedAt(LocalDateTime.now());
        companyRepository.save(company);

        // 🔥 탈퇴한 사용자가 관련된 알림 정리
        List<Notification> relatedNotifications =
                notificationRepository.findBySenderIdOrReceiverId(id, id);

        for (Notification notification : relatedNotifications) {
            boolean senderDeleted = companyRepository.findById(notification.getSenderId())
                    .map(c -> c.getDeletedAt() != null)
                    .orElse(true); // 회사 없으면 true

            boolean receiverDeleted = companyRepository.findById(notification.getReceiverId())
                    .map(c -> c.getDeletedAt() != null)
                    .orElse(true);

            if (senderDeleted && receiverDeleted) {
                notificationRepository.delete(notification);
            }
        }
    }

    public boolean existsByBusinessNumber(String businessNumber) {
        return companyRepository.existsByBusinessNumber(businessNumber);
    }

    public CompanyDto findActiveById(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        if (company.getDeletedAt() != null) {
            throw new RuntimeException("탈퇴된 회원입니다.");
        }

        return CompanyDto.fromEntity(company);
    }

    public List<CompanyDto> findAllActive() {
        return companyRepository.findAll().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(CompanyDto::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return companyRepository.existsById(id);
    }
}