package com.example.demo.controller;

import com.example.demo.domain.Ads;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.AdsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ad")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdsController {

    private final AdsService adsService;

    @Autowired
    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    // 1) 전체 광고 조회
    @GetMapping("/readMany")
    public ResponseEntity<?> getAllAds() {
        List<Ads> adsList = adsService.getAllAds();
        return ResponseEntity.ok(new ResponseDto<>(true, "전체 광고 조회 성공", adsList));
    }

    // 2) 광고 ID로 광고 조회
    @GetMapping("/{adsId}")
    public ResponseEntity<?> getAdById(@PathVariable String adsId) {
        Ads ad = adsService.getAdById(adsId);
        if (ad == null) {
            return ResponseEntity.status(404).body(new ResponseDto<>(false, "광고를 찾을 수 없습니다", null));
        }
        return ResponseEntity.ok(new ResponseDto<>(true, "광고 조회 성공", ad));
    }

    // 3) 광고 등록 (세션 기반)
    @PostMapping("/create")
    public ResponseEntity<?> createAd(@RequestBody Ads ad, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        ad.setCompanyId(companyId);
        Ads createdAd = adsService.createAd(ad);
        return ResponseEntity.ok(new ResponseDto<>(true, "광고 등록 성공", createdAd));
    }

    // 4) 광고 수정
    @PatchMapping("/update/{adsId}")
    public ResponseEntity<?> updateAd(@PathVariable String adsId, @RequestBody Ads updatedAd, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        try {
            Ads result = adsService.updateAd(adsId, updatedAd, companyId);
            return ResponseEntity.ok(new ResponseDto<>(true, "광고 수정 성공", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(new ResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 5) 광고 삭제 (세션 기반)
    @DeleteMapping("/delete/{adsId}")
    public ResponseEntity<?> deleteAd(@PathVariable String adsId, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        try {
            adsService.deleteAd(adsId, companyId);
            return ResponseEntity.ok(new ResponseDto<>(true, "광고 삭제 성공", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ResponseDto<>(false, e.getMessage(), null));
        }
    }
}
