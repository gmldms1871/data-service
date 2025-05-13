package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import com.example.demo.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 리뷰 목록 조회 (제품 ID 또는 기업 ID로 조회)
    @GetMapping
    public ResponseEntity<?> getReviews(
            @RequestParam(name = "productId", required = false) String productId,
            @RequestParam(name = "companyId", required = false) String companyId) {

        if (productId != null) {
            List<ReviewDto> reviews = reviewService.getReviewsByProductId(productId);
            System.out.println("Company Reviews: " + reviews);  // 리뷰 리스트 출력
            return ResponseEntity.ok(reviews);  // 제품 ID로 조회
        } else if (companyId != null) {
            List<ReviewDto> reviews = reviewService.getReviewsByCompanyId(companyId);
            System.out.println("Company Reviews: " + reviews);  // 리뷰 리스트 출력
            return ResponseEntity.ok(reviews);  // 기업 ID로 조회
        } else {
            return ResponseEntity.badRequest().body("productId 또는 companyId 중 하나는 필수입니다.");
        }
    }


    // 리뷰 작성 (거래 ID로 유효성 검증 후)
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDto dto) {
        System.out.println("Received ReviewDto: " + dto);  // 로그 출력

        // `createdAt` 필드를 현재 시간으로 자동 설정
        dto.setCreatedAt(LocalDateTime.now());

        try {
            reviewService.createReview(dto, dto.getTransactionId());  // 거래 ID로 유효성 체크 후 리뷰 작성
            return ResponseEntity.status(HttpStatus.CREATED).body("리뷰가 등록되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 리뷰 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable("id") String id, @RequestBody ReviewDto dto) {
        // 기존 리뷰를 DB에서 가져옴
        ReviewDto existing = reviewService.getReviewById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("리뷰를 찾을 수 없습니다.");
        }

        // ID는 pathVariable로 덮고, 아래 필드들은 DB값 유지
        dto.setId(id);
        dto.setProductId(existing.getProductId());
        dto.setCompanyId(existing.getCompanyId());
        dto.setTransactionId(existing.getTransactionId());
        dto.setCreatedAt(existing.getCreatedAt());

        // 나머지 필드만 갱신 (rating, review 등)
        reviewService.updateReview(dto);
        return ResponseEntity.ok("리뷰가 수정되었습니다.");
    }


    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") String id) {
        reviewService.deleteReview(id);  // 리뷰 삭제
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}
