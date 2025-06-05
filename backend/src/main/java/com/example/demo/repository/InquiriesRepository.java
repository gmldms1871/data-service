package com.example.demo.repository;

import com.example.demo.domain.Inquiries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiriesRepository extends JpaRepository<Inquiries, String> {
    // productId로 문의 목록 조회
    List<Inquiries> findByProductId(String productId);

    // companyId로 문의 목록 조회
    List<Inquiries> findByCompanyId(String companyId);

    //
    List<Inquiries> findByCompanyIdAndProductId(String companyId, String productId);
}


// JpaRepository가 save, findById는 기본 제공하니까 다시 선언할 필요 없음.
// Inquiry 엔티티에 대한 CRUD 처리를 위한 JPA 인터페이스
