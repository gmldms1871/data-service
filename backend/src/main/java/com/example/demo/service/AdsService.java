package com.example.demo.service;

import com.example.demo.domain.Ads;
import com.example.demo.repository.AdsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdsService {

    private final AdsRepository adsRepository;

    @Autowired
    public AdsService(AdsRepository adsRepository) {
        this.adsRepository = adsRepository;
    }

    // 전체 광고 리스트 조회
    public List<Ads> getAllAds() {
        return adsRepository.findAll();
    }

    // company_id(회사 ID)로 광고 조회
    public List<Ads> getAdsByCompanyId(String companyId) {
        return adsRepository.findByCompanyId(companyId);
    }

    // 광고 등록
    public Ads createAd(Ads ad) {
        return adsRepository.save(ad);
    }

    // 광고 삭제 (id는 String임)
    public void deleteAd(String id) {
        adsRepository.deleteById(id);
    }
}
