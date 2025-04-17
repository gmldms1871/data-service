package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company {
    @Id
    private String id;  // 기업 고유 식별자

    private String categoryId;

    @Column(unique = true)
    private String email;

    private String password;
    private String userName;
    private String userPhone;
    private String nickname;
    private String companyName;
    private String businessNumber;
    private String homepage;
    private String interestTopic;
    private java.time.LocalDateTime createdAt;
}