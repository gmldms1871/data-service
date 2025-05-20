package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Inquiries {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, length = 100)
    private String id;

    @Column(name = "company_id", length = 100)
    private String companyId;// 문의 대상 회사 ID,

    @Column(name = "product_id", length = 100)
    private String productId;         // 제품 ID // 추가

    @Column(name = "attachment_id", length = 100)
    private String attachmentId;    // 첨부 파일 ID

    private Integer budget;          // 예산 정보

    private String message;          // 문의 타이틀
    private String description;     // 문의 상세 내용

    private LocalDateTime editAddDate; // 문의 등록일시


}

// companyId로 조회 또는 전과 같ㅣ productId로 조회
//http://localhost:4601/api/inquiries?productId=1      =  4637fa67-a024-4a73-a11f-23ce4ca7ab6b

//{

//  "budgetmmffrf": 5000,
//  "message": "문의 제목",
//  "description": "문의 내용",
//  "attachmentId": null
//}

/**
 * multi part
 */