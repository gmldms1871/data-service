package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ads {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "company_id", nullable = false, length = 100)
    private String companyId;

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @Column(name = "ads_period", nullable = false)
    private Long adsPeriod;

    // ad_image → attachment_id 로 컬럼명 변경
    @Column(name = "attachment_id", nullable = false, length = 100)
    private String attachmentId;

    // 생성 시각 자동 채움
    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    // 삭제 시각 (soft-delete 용)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
