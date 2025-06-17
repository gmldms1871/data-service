package com.example.demo.controller;

import com.example.demo.dto.SearchResult;
import com.example.demo.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    @GetMapping("/readMany")
    public ResponseEntity<?> search(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String tag) {
        try {
            // "전체 조회는 허용하되, 빈 문자열만 들어온 경우는 예외" 처리
            boolean anyParamProvided = (q != null || category != null || tag != null);

            if (anyParamProvided &&
                    (q == null || q.trim().isEmpty()) &&
                    (category == null || category.trim().isEmpty()) &&
                    (tag == null || tag.trim().isEmpty())) {
                log.warn("검색 조건 누락: q, category, tag 모두 빈 문자열");
                throw new IllegalArgumentException("검색 조건을 최소 하나 이상 입력해주세요.");
            }

            List<SearchResult> results = searchService.search(q, category, tag);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 검색 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("검색 중 서버 오류 발생", e);
            return ResponseEntity.internalServerError().body("검색 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.");
        }
    }
}
