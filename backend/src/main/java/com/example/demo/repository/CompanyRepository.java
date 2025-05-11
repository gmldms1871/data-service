package com.example.demo.repository;

import com.example.demo.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findByEmail(String email);
    Optional<Company> findByEmailAndPassword(String email, String password);
}