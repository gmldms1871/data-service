package com.example.demo.repository;

import com.example.demo.domain.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    // 'companyEmail' 필드 관련 메서드 제거
     // 이건 유지
}
