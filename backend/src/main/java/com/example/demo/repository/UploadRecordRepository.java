package com.example.demo.repository;

import com.example.demo.domain.UploadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {
    Optional<UploadRecord> findByProductId(String productId);
}