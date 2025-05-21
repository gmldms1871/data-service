package com.example.demo.service;

import com.example.demo.domain.Company;
import com.example.demo.domain.Inquiries;
import com.example.demo.domain.Transaction;
import com.example.demo.repository.InquiriesRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InquiriesRepository inquiriesRepository;
    private final CompanyRepository companyRepository;

    /**
     * 거래 시작 (판매자만 가능)
     */
    public Transaction create(Transaction transaction, String requestingSellerId) {
        if (!requestingSellerId.equals(transaction.getSellerId())) {
            throw new RuntimeException("거래 생성 권한이 없습니다 (판매자만 가능).");
        }

        Inquiries inquiries = inquiriesRepository.findById(transaction.getBuyerId())
                .orElseThrow(() -> new RuntimeException("해당 buyer_id로 등록된 문의가 존재하지 않습니다."));

        transaction.setStatus(Transaction.Status.cancelled);
        transaction.setSellerConfirmed(false);
        transaction.setBuyerConfirmed(false);
        transaction.setIsDisabled(false);

        return transactionRepository.save(transaction);
    }

    /**
     * 거래 완료 버튼 클릭 (로그인 된 아이디)
     */
    public void confirmByLoginUser(String transactionId, String loginCompanyId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다."));

        String buyerCompanyId = null;

        if (tx.getBuyerId() != null) {
            Inquiries buyerInquiry = inquiriesRepository.findById(tx.getBuyerId())
                    .orElseThrow(() -> new RuntimeException("buyerId에 해당하는 문의 정보를 찾을 수 없습니다."));
            buyerCompanyId = buyerInquiry.getCompanyId();
        }

        // seller는 직접 비교, buyer는 inquries -> companyId로 역추적
        boolean isBuyer = loginCompanyId.equals(buyerCompanyId);
        boolean isSeller = loginCompanyId.equals(tx.getSellerId());

        if (!isBuyer && !isSeller) {
            throw new RuntimeException("이 거래에 대한 권한이 없습니다.");
        }

        // 확인 처리
        if (isBuyer) tx.setBuyerConfirmed(true);
        if (isSeller) tx.setSellerConfirmed(true);

        // 거래 상태 갱신 로직
        boolean buyer = Boolean.TRUE.equals(tx.getBuyerConfirmed());
        boolean seller = Boolean.TRUE.equals(tx.getSellerConfirmed());

        if (buyer && seller) {
            tx.setStatus(Transaction.Status.confirmed);
        } else if (buyer || seller) {
            tx.setStatus(Transaction.Status.pending);
        } else {
            tx.setStatus(Transaction.Status.cancelled);
        }

        transactionRepository.save(tx);
    }

    /**
     * 거래 목록 조회 (판매자 기준)
     */
    public List<Transaction> getTransactionsBySellerId(String sellerId) {
        return transactionRepository.findBySellerIdAndIsDisabledFalse(sellerId);
    }

    /**
     * 거래 상태 확인 (buyer 또는 seller만 가능)
     */
    public void validateConfirmedTransaction(String transactionId, String userId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("거래가 존재하지 않습니다."));

        if (!userId.equals(tx.getBuyerId()) && !userId.equals(tx.getSellerId())) {
            throw new RuntimeException("이 거래를 확인할 권한이 없습니다.");
        }

        if (tx.getStatus() != Transaction.Status.confirmed) {
            throw new RuntimeException("해당 거래는 아직 완료되지 않아 리뷰를 작성할 수 없습니다.");
        }
    }

    /**
     * 거래 삭제 (판매자만 가능)
     */
    public void deleteTransaction(String transactionId, String requestingSellerId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("해당 거래가 존재하지 않습니다."));

        if (!transaction.getSellerId().equals(requestingSellerId)) {
            throw new RuntimeException("해당 거래를 삭제할 권한이 없습니다 (판매자만 삭제 가능).");
        }

        transaction.setIsDisabled(true);
        transaction.setDeleteDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다."));
    }
}
