package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", length = 100)
    private String id; // 거래 고유 ID

    @Column(name = "buyer_id", nullable = false, length = 100)
    private String buyerId; // 문의 ID (inquiries.id) — 구매자 식별용

    @Column(name = "seller_id", nullable = false, length = 100)
    private String sellerId; // 판매자 이메일 (company.id)

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId; // 거래 상품 ID

    @Column(name = "seller_confirmed")
    private Boolean sellerConfirmed; // 판매자가 완료 버튼 눌렀는지 여부

    @Column(name = "buyer_confirmed")
    private Boolean buyerConfirmed; // 구매자가 완료 버튼 눌렀는지 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status; // 거래 상태 (pending, confirmed, cancelled)

    @Column(name = "is_disabled")
    private Boolean isDisabled; // 거래 비활성 여부 (소프트 삭제 등)

    @Column(name = "create_date")
    private LocalDateTime createDate; // 생성 일시

    @Column(name = "update_date")
    private LocalDateTime updateDate; // 수정 일시

    @Column(name = "delete_date")
    private LocalDateTime deleteDate; // 삭제 일시 (소프트 삭제용)

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now;
        this.updateDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public enum Status {
        pending,     // 거래 대기 중
        confirmed,   // 거래 완료
        cancelled    // 거래 취소
    }
}
