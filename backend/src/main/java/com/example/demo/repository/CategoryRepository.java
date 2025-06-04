package com.example.demo.repository;

import com.example.demo.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // 필요에 따라 사용자 정의 쿼리 메소드 추가 가능
    // 예: 타입별 카테고리 조회
    // List<Category> findByType(String type);
}