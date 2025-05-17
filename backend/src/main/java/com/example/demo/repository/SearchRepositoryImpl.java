package com.example.demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.SearchResult;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<SearchResult> search(String keyword, String category, String tag) {
        String jpql = """
            SELECT new com.example.demo.dto.SearchResult(
                p.id, p.name, c.companyName, cat.name
            )
            FROM Products p
            JOIN p.company c
            JOIN p.category cat
            LEFT JOIN p.productTags pt
            LEFT JOIN pt.tag t
            WHERE 
                (:keyword IS NULL OR
                    p.name LIKE CONCAT('%', :keyword, '%') OR
                    c.companyName LIKE CONCAT('%', :keyword, '%') OR
                    cat.name LIKE CONCAT('%', :keyword, '%') OR
                    t.name LIKE CONCAT('%', :keyword, '%'))
                AND (:category IS NULL OR cat.name = :category)
                AND (:tag IS NULL OR t.name = :tag)
            GROUP BY p.id, p.name, c.companyName, cat.name
            """;

        return em.createQuery(jpql, SearchResult.class)
                .setParameter("keyword", keyword)
                .setParameter("category", category)
                .setParameter("tag", tag)
                .getResultList();
    }
}
