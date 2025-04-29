package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 거래 고유 ID

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId; // 문의 ID (inquiries.id) — 구매자 식별용

    @Column(name = "seller_id", nullable = false, length = 100)
    private String sellerId; // 판매자 이메일 (company.id)

    @Column(name = "product_id", nullable = false)
    private Long productId; // 거래 상품 ID

    @Column(name = "seller_confirmed")
    private Boolean sellerConfirmed; // 판매자가 완료 버튼 눌렀는지 여부

    @Column(name = "buyer_confirmed")
    private Boolean buyerConfirmed; // 구매자가 완료 버튼 눌렀는지 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status; // 거래 상태 (pending, confirmed, cancelled)

    @Column(name = "transaction_at")
    private LocalDateTime transactionAt; // 거래 시작 시각

    public enum Status {
        pending,     // 거래 대기 중
        confirmed,   // 거래 완료
        cancelled    // 거래 취소 (현재 사용 안 함)
    }
}
