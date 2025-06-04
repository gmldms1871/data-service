package com.example.demo.controller;

import com.example.demo.domain.Category;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping
    public ResponseEntity<ResponseDto<?>> getAllCategories() {
        try {
            List<Category> categories = categoryService.findAllCategories();
            // categories가 null일 경우를 대비하여 빈 리스트로 초기화하는 것이 더 안전할 수 있습니다.
            // findAllCategories()가 null을 반환하지 않도록 보장하는 것이 가장 좋습니다.
            if (categories == null || categories.isEmpty()) {
                logger.info("No categories found.");
                // 데이터가 없을 때 200 OK와 함께 빈 리스트 또는 적절한 메시지를 반환할 수 있습니다.
                return ResponseEntity.ok(new ResponseDto<>(true, "No categories found", Collections.emptyList()));
            }
            logger.info("Successfully retrieved {} categories.", categories.size());
            return ResponseEntity.ok(new ResponseDto<>(true, "Successfully retrieved categories", categories));
        } catch (Exception e) {
            logger.error("Error while retrieving categories: {}", e.getMessage(), e); // 예외 로깅
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(false, "카테고리 조회 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}