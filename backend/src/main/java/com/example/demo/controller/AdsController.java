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
@RequestMapping("/api/ads")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdsController {

    private final AdsService adsService;

    @Autowired
    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    // 1) 전체 광고 조회
    @GetMapping
    public ResponseEntity<?> getAllAds() {
        List<Ads> adsList = adsService.getAllAds();
        return ResponseEntity.ok(new ResponseDto<>(true, "전체 광고 조회 성공", adsList));
    }

    // 2) 내 광고 조회 (세션 기반)
    @GetMapping("/me")
    public ResponseEntity<?> getMyAds(HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        List<Ads> adsList = adsService.getAdsByCompanyId(companyId);
        return ResponseEntity.ok(new ResponseDto<>(true, "내 광고 조회 성공", adsList));
    }

    // 3) 광고 등록 (세션 기반)
    @PostMapping
    public ResponseEntity<?> createAd(@RequestBody Ads ad, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        ad.setCompanyId(companyId); // 세션에서 companyId 설정
        Ads createdAd = adsService.createAd(ad);
        return ResponseEntity.ok(new ResponseDto<>(true, "광고 등록 성공", createdAd));
    }

    // 4) 광고 삭제 (세션 기반)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAd(@PathVariable String id, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(401).body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        try {
            adsService.deleteAd(id, companyId);
            return ResponseEntity.ok(new ResponseDto<>(true, "광고 삭제 성공", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ResponseDto<>(false, e.getMessage(), null));
        }
    }
}
