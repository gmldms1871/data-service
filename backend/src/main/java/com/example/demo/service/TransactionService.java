package com.example.demo.service;

import com.example.demo.domain.Transaction;
import com.example.demo.domain.Company;
import com.example.demo.domain.Inquiries;
import com.example.demo.domain.Products;
import com.example.demo.repository.InquiriesRepository;
import com.example.demo.repository.ProductsRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InquiriesRepository inquiriesRepository;
    private final CompanyRepository companyRepository;
    private final ProductsRepository productsRepository;

    /**
     * 거래 시작 (판매자만 가능)
     */
    public Transaction create(Transaction transaction, String loginCompanyId) {
        // 1. 해당 productId로 상품 조회
        Products product = productsRepository.findById(transaction.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 productId로 등록된 상품이 존재하지 않습니다."));

        // 2. 상품의 companyId (즉, seller)와 로그인한 회사 ID 비교
        if (!product.getCompanyId().equals(loginCompanyId)) {
            throw new IllegalArgumentException("해당 상품의 판매자만 거래를 생성할 수 있습니다.");
        }

        // 3. buyerId가 유효한지 확인
        Inquiries inquiries = inquiriesRepository.findById(transaction.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("해당 buyerId로 등록된 문의가 존재하지 않습니다."));

        // 4. 거래 기본 세팅
        transaction.setSellerId(loginCompanyId); // sellerId는 세션에서 세팅
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
                .orElseThrow(() -> new IllegalArgumentException("거래를 찾을 수 없습니다."));

        String buyerCompanyId = null;
        if (tx.getBuyerId() != null) {
            Inquiries buyerInquiry = inquiriesRepository.findById(tx.getBuyerId())
                    .orElseThrow(() -> new IllegalArgumentException("buyerId에 해당하는 문의 정보가 없습니다."));
            buyerCompanyId = buyerInquiry.getCompanyId();
        }

        boolean isBuyer = loginCompanyId.equals(buyerCompanyId);
        boolean isSeller = loginCompanyId.equals(tx.getSellerId());

        if (!isBuyer && !isSeller) {
            throw new IllegalArgumentException("이 거래에 대한 접근 권한이 없습니다.");
        }

        if (isBuyer) tx.setBuyerConfirmed(true);
        if (isSeller) tx.setSellerConfirmed(true);

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
                .orElseThrow(() -> new IllegalArgumentException("거래가 존재하지 않습니다."));

        String buyerCompanyId = null;
        if (tx.getBuyerId() != null) {
            Inquiries buyerInquiry = inquiriesRepository.findById(tx.getBuyerId())
                    .orElse(null);
            if (buyerInquiry != null) {
                buyerCompanyId = buyerInquiry.getCompanyId();
            }
        }

        boolean isBuyer = userId.equals(buyerCompanyId);
        boolean isSeller = userId.equals(tx.getSellerId());

        if (!isBuyer && !isSeller) {
            throw new IllegalArgumentException("이 거래에 접근할 권한이 없습니다.");
        }

        if (tx.getStatus() != Transaction.Status.confirmed) {
            throw new IllegalArgumentException("거래가 완료되지 않아 리뷰 작성이 불가능합니다.");
        }
    }

    /**
     * 거래 삭제 (판매자만 가능)
     */
    public void deleteTransaction(String transactionId, String loginCompanyId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래가 존재하지 않습니다."));

        // ✅ 완료된 거래는 삭제 불가
        if (tx.getStatus() == Transaction.Status.confirmed) {
            throw new IllegalStateException("완료된 거래는 삭제할 수 없습니다.");
        }

        // ✅ 로그인 회사가 seller인지 확인
        if (!loginCompanyId.equals(tx.getSellerId())) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // ✅ 소프트 삭제 처리
        tx.setIsDisabled(true);
        tx.setDeleteDate(LocalDateTime.now()); // delete_date
        transactionRepository.save(tx);
    }

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("거래를 찾을 수 없습니다."));
    }
}
