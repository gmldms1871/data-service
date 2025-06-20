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
        List<Ads> ads = adsRepository.findByCompanyId(companyId);
        return ads != null ? ads : List.of(); // 방어적 코드
    }
    // 광고 등록: adsPeriod, createAt, deletedAt 계산 후 저장
    public Ads createAd(Ads ad) {
        // 중복 제목 검사
        if (adsRepository.existsByCompanyIdAndTitle(ad.getCompanyId(), ad.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 동일한 제목의 광고가 존재합니다.");
        }

        ad.setDeletedAt(LocalDateTime.now().plusDays(ad.getAdsPeriod()));
        return adsRepository.save(ad);
    }

    // 광고 단건 조회 (삭제·권한검사용)
    public Ads getAdById(String id) {
        return adsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 광고를 찾을 수 없습니다: " + id));
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

    // 광고 수정
    public Ads updateAd(String id, Ads updatedAd, String companyId) {
        Ads existing = getAdById(id);

        if (!existing.getCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 광고만 수정할 수 있습니다.");
        }

        // 제목 변경 시, 중복 제목 검사
        boolean isTitleChanged = !existing.getTitle().equals(updatedAd.getTitle());
        if (isTitleChanged && adsRepository.existsByCompanyIdAndTitle(companyId, updatedAd.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 동일한 제목의 광고가 존재합니다.");
        }

        if (updatedAd.getTitle() != null) {
            existing.setTitle(updatedAd.getTitle());
        }
        if (updatedAd.getContent() != null) {
            existing.setContent(updatedAd.getContent());
        }
        if (updatedAd.getImageUrl() != null) {
            existing.setImageUrl(updatedAd.getImageUrl());
        }
        if (updatedAd.getAdsPeriod() != null) {
            existing.setAdsPeriod(updatedAd.getAdsPeriod());
            existing.setDeletedAt(LocalDateTime.now().plusDays(updatedAd.getAdsPeriod()));
        }

        return adsRepository.save(existing);
    }
}
