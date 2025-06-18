package com.example.demo.service;

import com.example.demo.dto.ReviewDto;
import com.example.demo.exception.review.InvalidTransactionException;
import com.example.demo.exception.review.ReviewAlreadyExistsException;
import com.example.demo.exception.review.UnauthorizedReviewAccessException;
import com.example.demo.mapper.ReviewMapper;
import com.example.demo.mapper.TransactionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final TransactionMapper transactionMapper;

    // 생성자 주입 (SqlSession 제거됨)
    public ReviewServiceImpl(ReviewMapper reviewMapper, TransactionMapper transactionMapper) {
        this.reviewMapper = reviewMapper;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<ReviewDto> getReviewsByProductId(String productId) {
        List<ReviewDto> reviews = reviewMapper.findByProductId(productId);
        System.out.println("Fetched Reviews: " + reviews);
        return reviews;
    }

    @Override
    public List<ReviewDto> getReviewsByCompanyId(String companyId) {
        return reviewMapper.findByCompanyId(companyId);
    }

    @Override
    public void createReview(ReviewDto dto, String transactionId) {
        int exists = reviewMapper.existsByTransactionId(transactionId);
        if (exists == 1) {
            throw new ReviewAlreadyExistsException();
        }

        boolean isConfirmed = isTransactionConfirmed(transactionId);
        if (!isConfirmed) {
            throw new InvalidTransactionException();
        }

        String buyerCompanyId = transactionMapper.getCompanyIdFromBuyerInquiry(transactionId);
        if (!dto.getCompanyId().equals(buyerCompanyId)) {
            throw new UnauthorizedReviewAccessException();
        }

        reviewMapper.insertReview(dto);
    }

    @Override
    public void updateReview(String id, ReviewDto dto) {
        ReviewDto existing = reviewMapper.findById(dto.getId());
        if (existing == null) {
            throw new IllegalStateException("기존 리뷰가 존재하지 않습니다.");
        }

        if (isDifferent(existing.getProductId(), dto.getProductId()) ||
                isDifferent(existing.getCompanyId(), dto.getCompanyId()) ||
                isDifferent(existing.getTransactionId(), dto.getTransactionId())) {
            throw new IllegalStateException("productId, companyId, transactionId는 수정할 수 없습니다.");
        }

        if (existing.getCreatedAt() != null && !existing.getCreatedAt().equals(dto.getCreatedAt())) {
            throw new IllegalStateException("createdAt은 수정할 수 없습니다.");
        }

        reviewMapper.updateReview(dto);
    }

    @Override
    public int deleteReview(String id) {
        return reviewMapper.deleteReview(id); // 삭제된 행 수 리턴
    }

    // 🔄 변경된 부분: SqlSession 제거, Mapper 직접 호출
    @Override
    public boolean isTransactionConfirmed(String transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID는 필수입니다.");
        }

        Integer sellerConfirmed = transactionMapper.getSellerConfirmed(transactionId);
        Integer buyerConfirmed = transactionMapper.getBuyerConfirmed(transactionId);

        if (sellerConfirmed == null || buyerConfirmed == null) {
            throw new IllegalStateException("거래 상태를 확인할 수 없습니다.");
        }

        return sellerConfirmed == 1 && buyerConfirmed == 1;
    }

    private boolean isDifferent(String a, String b) {
        return (a != null && !a.equals(b)) || (a == null && b != null);
    }

    @Override
    public ReviewDto getReviewById(String id) {
        return reviewMapper.findById(id);
    }
}
