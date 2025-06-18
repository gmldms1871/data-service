package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, length = 100)
    private String id;

    @Column(name = "category_id", length = 100)
    private String categoryId;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_name", length = 50, nullable = false)
    private String userName;

    @Column(name = "user_phone", length = 50, nullable = false)
    private String userPhone;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "profile_attachment_url")
    private String profileAttachmentUrl;

    @Column(name = "business_number")
    private String businessNumber;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at", updatable = false)
    private LocalDateTime deletedAt;
}