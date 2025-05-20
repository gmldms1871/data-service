package com.example.demo.repository;

import com.example.demo.domain.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, String> {
    List<Products> findAllByCompanyId(String companyId);
}
