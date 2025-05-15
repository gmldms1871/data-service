package com.example.demo.service;

import com.example.demo.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getReviewsByProductId(String productId);
    List<ReviewDto> getReviewsByCompanyId(String companyId);
    void createReview(ReviewDto dto, String transactionId);
    void updateReview(ReviewDto dto);
    void deleteReview(String id);
    boolean isTransactionConfirmed(String transactionId); // 이 메서드만 남깁니다.
    // ReviewService.java
    ReviewDto getReviewById(String id);
}
