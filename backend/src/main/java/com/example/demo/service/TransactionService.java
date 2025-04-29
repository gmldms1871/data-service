package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.Transaction;
import com.example.demo.domain.Transaction.Status;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.InquiriesRepository;
import com.example.demo.domain.Inquiries;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.repository.CompanyRepository;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InquiriesRepository inquiriesRepository;
    private final CompanyRepository companyRepository;

    /**
     * 거래 시작
     * - seller만 가능
     * - 해당 buyer_id에 대한 문의가 존재해야 함
     */
    public Transaction create(Transaction transaction, String requestingSellerEmail) {

        // seller 권한 확인
        if (!requestingSellerEmail.equals(transaction.getSellerId())) {
            throw new RuntimeException("거래 생성 권한이 없습니다 (판매자만 가능).");
        }

        // buyer_id에 해당하는 문의가 실제로 존재하는지 검증
        Inquiries inquiries = inquiriesRepository.findById(transaction.getBuyerId())
                .orElseThrow(() -> new RuntimeException("해당 buyer_id로 등록된 문의가 존재하지 않습니다."));

        // 거래 기본 상태 설정
        transaction.setStatus(Status.pending);
        transaction.setTransactionAt(LocalDateTime.now());
        transaction.setSellerConfirmed(false);
        transaction.setBuyerConfirmed(false);

        return transactionRepository.save(transaction);
    }

    /**
     * 거래 완료 버튼 클릭
     * - buyer 또는 seller 중 하나가 완료하면 해당 필드 true로 설정
     * - 양쪽 모두 true일 경우 거래 상태를 confirmed로 변경
     */
    public void confirm(Long id, String role) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다."));

        if ("buyer".equalsIgnoreCase(role)) {
            tx.setBuyerConfirmed(true);
        } else if ("seller".equalsIgnoreCase(role)) {
            tx.setSellerConfirmed(true);
        } else {
            throw new RuntimeException("role 파라미터는 'buyer' 또는 'seller'여야 합니다.");
        }

        if (Boolean.TRUE.equals(tx.getBuyerConfirmed()) && Boolean.TRUE.equals(tx.getSellerConfirmed())) {
            tx.setStatus(Status.confirmed);
        }

        transactionRepository.save(tx);
    }

    /**
     * 내가 거래한 목록 조회
     * - userId: 나의 id (inquiries.id 기준)
     * - userEmail: 나의 이메일 (seller 기준)
     */
//    public List<Transaction> getMyTransactions(Long userId, String userEmail) {
//        return transactionRepository.findByBuyerIdOrSellerId(userId, userEmail);
//    }
    // 이메일 기준으로 거래 목록 조회 메서드 추가
    public List<Transaction> getTransactionsBySellerEmail(String userEmail) {
        // 이메일로 회사 UUID 찾기
        Company seller = companyRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("해당 이메일로 등록된 회사가 없습니다."));

        System.out.println("▶ seller UUID: " + seller.getId());

        // UUID로 거래 검색
        List<Transaction> transactions = transactionRepository.findBySellerId(seller.getId());

        System.out.println("▶ transactions.size: " + transactions.size());

        for (Transaction t : transactions) {
            System.out.println("▶ transaction.id: " + t.getId() + ", status: " + t.getStatus());
        }

        return transactions;
    }

    /**
     * 특정 거래가 완료되었는지 확인 → 리뷰 작성 전 권한 검사에 사용 가능
     */
    public void validateConfirmedTransaction(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("거래가 존재하지 않습니다."));
        if (tx.getStatus() != Status.confirmed) {
            throw new RuntimeException("해당 거래는 아직 완료되지 않아 리뷰를 작성할 수 없습니다.");
        }
    }
}
