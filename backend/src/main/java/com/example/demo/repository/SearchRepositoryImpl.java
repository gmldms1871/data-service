package com.example.demo.repository;

import com.example.demo.dto.SearchResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchRepositoryImpl implements SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SearchResult> search(String keyword, String category, String tag) {
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
    }
}