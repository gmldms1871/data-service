package com.example.demo.repository;

import com.example.demo.dto.SearchResult;
import java.util.List;

public interface SearchRepository {
    List<SearchResult> search(String keyword, String category, String tag);
}