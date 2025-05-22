package com.example.demo.dto;

import com.example.demo.domain.Company;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyDto {
    private String id;
    private String categoryId;
    private String email;
    private String userName;
    private String userPhone;
    private String nickname;
    private String companyName;
    private String profileAttachmentUrl;
    private String businessNumber;
    private String homepage;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환
    public static CompanyDto fromEntity(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setCategoryId(company.getCategoryId());
        dto.setEmail(company.getEmail());
        dto.setUserName(company.getUserName());
        dto.setUserPhone(company.getUserPhone());
        dto.setNickname(company.getNickname());
        dto.setCompanyName(company.getCompanyName());
        dto.setProfileAttachmentUrl(company.getProfileAttachmentUrl());
        dto.setBusinessNumber(company.getBusinessNumber());
        dto.setHomepage(company.getHomepage());
        dto.setCreatedAt(company.getCreatedAt());
        return dto;
    }
}
