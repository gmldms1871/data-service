package com.example.demo.repository;

import com.example.demo.domain.Inquiries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiriesRepository extends JpaRepository<Inquiries, Long> {
    // 기본 제공 메서드: findById(id) 사용 가능
}
