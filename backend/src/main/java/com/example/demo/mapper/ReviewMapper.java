package com.example.demo.mapper;

import com.example.demo.dto.ReviewDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReviewMapper {
    List<ReviewDto> findByProductId(String productId);  // 제품 ID로 리뷰 조회
    List<ReviewDto> findByCompanyId(String companyId);  // 회사 ID로 리뷰 조회
    void insertReview(ReviewDto reviewDto);  // 리뷰 등록
    void updateReview(ReviewDto dto);  // 리뷰 수정
    int deleteReview(String id);  // 리뷰 삭제
    ReviewDto findById(String id);
    int existsByTransactionId(String transactionId);

}
