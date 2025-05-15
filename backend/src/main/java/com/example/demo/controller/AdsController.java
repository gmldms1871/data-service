// AdsController.java
package com.example.demo.controller;

import com.example.demo.domain.Ads;
import com.example.demo.service.AdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdsController {

    private final AdsService adsService;

    @Autowired
    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    // 1) 전체 광고 조회
    @GetMapping
    public ResponseEntity<List<Ads>> getAllAds() {
        return ResponseEntity.ok(adsService.getAllAds());
    }

    // 2) 내 광고 조회 (companyId 파라미터)
    @GetMapping(params = "companyId")
    public ResponseEntity<List<Ads>> getMyAds(@RequestParam String companyId) {
        return ResponseEntity.ok(adsService.getAdsByCompanyId(companyId));
    }

    // 3) 광고 등록 (adsPeriod 포함)
    @PostMapping
    public ResponseEntity<Ads> createAd(@RequestBody Ads ad) {
        Ads created = adsService.createAd(ad);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 4) 광고 삭제 (companyId 검증 포함)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(
            @PathVariable String id,
            @RequestParam String companyId
    ) {
        adsService.deleteAd(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
