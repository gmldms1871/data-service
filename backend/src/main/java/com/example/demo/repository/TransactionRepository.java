package com.example.demo.repository;

import com.example.demo.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 특정 사용자와 관련된 거래 조회 (buyer 또는 seller로 참여한 경우)
    List<Transaction> findByBuyerIdOrSellerId(Long buyerId, String sellerId);
    // 이메일로 seller 거래 목록 조회
    List<Transaction> findBySellerId(String sellerId);

}

