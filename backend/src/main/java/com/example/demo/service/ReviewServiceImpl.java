package com.example.demo.service;

import com.example.demo.dto.ReviewDto;
import com.example.demo.mapper.ReviewMapper;
import com.example.demo.mapper.TransactionMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final TransactionMapper transactionMapper;
    private final SqlSession sqlSession;

    // 생성자 주입
    public ReviewServiceImpl(ReviewMapper reviewMapper, TransactionMapper transactionMapper, SqlSession sqlSession) {
        this.reviewMapper = reviewMapper;
        this.transactionMapper = transactionMapper;
        this.sqlSession = sqlSession;
    }

    // 제품 ID로 리뷰 조회
    @Override
    public List<ReviewDto> getReviewsByProductId(String productId) {
        List<ReviewDto> reviews = reviewMapper.findByProductId(productId);
        System.out.println("Fetched Reviews: " + reviews);  // 쿼리에서 반환된 리뷰들 확인
        return reviews;
    }

    // 회사 ID로 리뷰 조회
    @Override
    public List<ReviewDto> getReviewsByCompanyId(String companyId) {
        return reviewMapper.findByCompanyId(companyId); // 회사 ID로 리뷰 조회
    }

    // 리뷰 작성
    @Override
    public void createReview(ReviewDto dto, String transactionId) {
        // transactionId만으로 거래 상태 확인
        boolean isConfirmed = isTransactionConfirmed(transactionId);
        if (!isConfirmed) {
            throw new IllegalStateException("리뷰를 작성할 수 있는 거래가 아닙니다.");
        }

        // 리뷰 등록
        reviewMapper.insertReview(dto); // 리뷰 등록
    }

    // 리뷰 수정
    @Override
    public void updateReview(ReviewDto dto) {
        ReviewDto existing = reviewMapper.findById(dto.getId());
        if (existing == null) {
            throw new IllegalStateException("기존 리뷰가 존재하지 않습니다.");
        }

        // 🔒 수정 불가 필드 변경 감지
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

    // 안전한 문자열 비교 메서드
    private boolean isDifferent(String a, String b) {
        return (a != null && !a.equals(b)) || (a == null && b != null);
    }



    // 리뷰 삭제
    @Override
    public void deleteReview(String id) {
        reviewMapper.deleteReview(id); // 리뷰 삭제
    }

    // 거래 확인
    @Override
    public boolean isTransactionConfirmed(String transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID는 필수입니다.");
        }

        // transactionId만 사용하여 트랜잭션 상태를 확인
        Map<String, Object> params = Map.of("transactionId", transactionId);

        Integer sellerConfirmed = sqlSession.selectOne("com.example.demo.mapper.TransactionMapper.getSellerConfirmed", params);
        Integer buyerConfirmed = sqlSession.selectOne("com.example.demo.mapper.TransactionMapper.getBuyerConfirmed", params);

        // seller_confirmed와 buyer_confirmed가 모두 1인 경우만 true 반환
        if (sellerConfirmed == null || buyerConfirmed == null) {
            throw new IllegalStateException("거래 상태를 확인할 수 없습니다.");
        }

        return sellerConfirmed == 1 && buyerConfirmed == 1;
    }
    // ReviewServiceImpl.java
    @Override
    public ReviewDto getReviewById(String id) {
        return reviewMapper.findById(id);  // MyBatis or JDBC로 조회
    }

}
