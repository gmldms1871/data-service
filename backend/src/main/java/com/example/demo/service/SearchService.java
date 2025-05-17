package com.example.demo.service;

import com.example.demo.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SearchResult;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    public List<SearchResult> search(String keyword, String category, String tag) {
        return searchRepository.search(
                isBlank(keyword) ? null : keyword,
                isBlank(category) ? null : category,
                isBlank(tag) ? null : tag
        );
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}

