package com.example.demo.repository;

import com.example.demo.domain.Ads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsRepository extends JpaRepository<Ads, String> {

    // 회사 ID(company_id)로 광고 조회
    List<Ads> findByCompanyId(String companyId);
}
