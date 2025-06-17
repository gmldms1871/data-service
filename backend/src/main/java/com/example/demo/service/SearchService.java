package com.example.demo.service;

import com.example.demo.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SearchResult;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
    private final SearchRepository searchRepository;

    public List<SearchResult> search(String keyword, String category, String tag) {

        try {
            return searchRepository.search(
                    isBlank(keyword) ? null : keyword,
                    isBlank(category) ? null : category,
                    isBlank(tag) ? null : tag
            );
        } catch (Exception e) {
            log.error("검색 서비스 처리 중 오류 발생", e);
            throw new RuntimeException("검색 중 문제가 발생했습니다.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
