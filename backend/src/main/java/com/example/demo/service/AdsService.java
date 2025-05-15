// AdsService.java
package com.example.demo.service;

import com.example.demo.domain.Ads;
import com.example.demo.repository.AdsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdsService {

    private final AdsRepository adsRepository;

    @Autowired
    public AdsService(AdsRepository adsRepository) {
        this.adsRepository = adsRepository;
    }

    // 전체 광고 조회
    public List<Ads> getAllAds() {
        return adsRepository.findAll();
    }

    // 회사별 광고 조회 (마이페이지)
    public List<Ads> getAdsByCompanyId(String companyId) {
        return adsRepository.findByCompanyId(companyId);
    }

    // 광고 등록: adsPeriod, createAt, deletedAt 계산 후 저장
    public Ads createAd(Ads ad) {
        LocalDateTime now = LocalDateTime.now();
        ad.setCreateAt(now);
        ad.setDeletedAt(now.plusDays(ad.getAdsPeriod()));
        return adsRepository.save(ad);
    }

    // 광고 단건 조회 (삭제·권한검사용)
    public Ads getAdById(String id) {
        return adsRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("해당 광고를 찾을 수 없습니다: " + id)
                );
    }

    // 광고 삭제: 실제 삭제, 본인 회사만
    public void deleteAd(String id, String companyId) {
        Ads ad = getAdById(id);

        if (!ad.getCompanyId().equals(companyId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인의 광고만 삭제할 수 있습니다."
            );
        }

        adsRepository.delete(ad);
    }
}
