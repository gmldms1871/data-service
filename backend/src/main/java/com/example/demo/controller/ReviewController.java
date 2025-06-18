package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import com.example.demo.exception.review.ReviewNotFoundException;
import com.example.demo.exception.review.UnauthorizedReviewAccessException;
import com.example.demo.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/readMany/{productId}")
    public ResponseEntity<?> getReviewsByProductId(@PathVariable String productId) {
        List<ReviewDto> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(Map.of(
                "message", "제품 리뷰 목록 조회 성공",
                "reviews", reviews
        ));
    }

    @GetMapping("/readMany")
    public ResponseEntity<?> getReviewsByCompanyId(HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            throw new UnauthorizedReviewAccessException();
        }

        List<ReviewDto> reviews = reviewService.getReviewsByCompanyId(companyId);
        return ResponseEntity.ok(Map.of(
                "message", "회사 리뷰 목록 조회 성공",
                "reviews", reviews
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewDto dto, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            throw new UnauthorizedReviewAccessException();
        }

        dto.setId(UUID.randomUUID().toString());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setCompanyId(loginCompanyId);

        reviewService.createReview(dto, dto.getTransactionId());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "리뷰가 등록되었습니다."
        ));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable("id") String id,
                                          @RequestBody ReviewDto dto,
                                          HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            throw new UnauthorizedReviewAccessException();
        }

        ReviewDto existing = reviewService.getReviewById(id);
        if (existing == null) {
            throw new ReviewNotFoundException();
        }

        if (!existing.getCompanyId().equals(loginCompanyId)) {
            throw new UnauthorizedReviewAccessException();
        }



        dto.setId(id);
        dto.setProductId(existing.getProductId());
        dto.setCompanyId(loginCompanyId);
        dto.setTransactionId(existing.getTransactionId());
        dto.setCreatedAt(existing.getCreatedAt());

        reviewService.updateReview(id, dto);
        return ResponseEntity.ok(Map.of(
                "message", "리뷰가 수정되었습니다."
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") String id, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            throw new UnauthorizedReviewAccessException();
        }

        ReviewDto existing = reviewService.getReviewById(id);
        if (existing == null) {
            throw new ReviewNotFoundException();
        }

        if (!existing.getCompanyId().equals(companyId)) {
            throw new UnauthorizedReviewAccessException();
        }

        reviewService.deleteReview(id);
        return ResponseEntity.ok(Map.of(
                "message", "리뷰가 삭제되었습니다."
        ));
    }
}
