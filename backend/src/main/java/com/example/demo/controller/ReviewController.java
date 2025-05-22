package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import com.example.demo.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
            @RequestParam(name = "companyId", required = false) String companyId,
            HttpSession session) {

        if (productId != null) {
            List<ReviewDto> reviews = reviewService.getReviewsByProductId(productId);
            System.out.println("Product Reviews: " + reviews);
            return ResponseEntity.ok(reviews);
        }

        // ❗ companyId 파라미터가 없으면 로그인 정보로 대체
        if (companyId == null) {
            companyId = (String) session.getAttribute("loginCompanyId");
            if (companyId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }
        }

        List<ReviewDto> reviews = reviewService.getReviewsByCompanyId(companyId);
        System.out.println("Company Reviews: " + reviews);
        return ResponseEntity.ok(reviews);
    }


    // 리뷰 작성 (거래 ID로 유효성 검증 후)
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDto dto, HttpSession session) {
        System.out.println("Received ReviewDto: " + dto);  // 로그 출력

        dto.setId(UUID.randomUUID().toString());
        dto.setCreatedAt(LocalDateTime.now());

        // ✅ 로그인 세션에서 companyId 추출하여 dto에 설정
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        dto.setCompanyId(loginCompanyId);

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
        int deleted = reviewService.deleteReview(id);
        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("리뷰를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}