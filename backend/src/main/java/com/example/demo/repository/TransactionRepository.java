package com.example.demo.repository;

import com.example.demo.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // 특정 사용자와(transaction.id) 관련된 거래 조회
    List<Transaction> findByBuyerIdOrSellerId(String buyerId, String sellerId);

    // seller 거래 목록 조회
    List<Transaction> findBySellerId(String sellerId);

    // 실 거래 목록 조회 (삭제되지 않은 목록)
    List<Transaction> findBySellerIdAndIsDisabledFalse(String sellerId);

    // 거래 중복 방지
    boolean existsByBuyerIdAndProductId(String buyerId, String productId);

    // 거래 권한 확인 (판매자의 것인지 확인)
    boolean existsByIdAndSellerId(String transactionId, String sellerId);

    // 상태 필터링 (완료된 거래는 삭제 할 수 없도록 예외처리 추가함)
    List<Transaction> findBySellerIdAndStatus(String sellerId, Transaction.Status status);
}
