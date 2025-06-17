package com.example.demo.repository;

import com.example.demo.dto.SearchResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchRepositoryImpl implements SearchRepository {

    private static final Logger log = LoggerFactory.getLogger(SearchRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SearchResult> search(String keyword, String category, String tag) {
        try {
            String jpql = "SELECT DISTINCT new com.example.demo.dto.SearchResult(" +
                    "p.id, p.name, c.name, t.name) " +
                    "FROM Products p " +
                    "LEFT JOIN ProductTag pt ON pt.product = p " +
                    "LEFT JOIN Tag t ON pt.tag = t " +
                    "LEFT JOIN Category c ON p.categoryId = c.id " +
                    "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "AND (:category IS NULL OR LOWER(c.name) = LOWER(:category)) " +
                    "AND (:tag IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tag, '%')))";

            return entityManager.createQuery(jpql, SearchResult.class)
                    .setParameter("keyword", keyword)
                    .setParameter("category", category)
                    .setParameter("tag", tag)
                    .getResultList();

        } catch (Exception e) {
            log.error("JPQL 검색 실행 중 오류 발생", e);
            throw new RuntimeException("데이터베이스 검색 중 오류가 발생했습니다.");
        }
    }
}
