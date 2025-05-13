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

    // 메인 페이지: 전체 광고 리스트 조회
    @GetMapping
    public ResponseEntity<List<Ads>> getAllAds() {
        List<Ads> ads = adsService.getAllAds();
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    // My 페이지: 나의 광고 목록 조회 (companyId로 조회)
    @GetMapping(params = "companyId")
    public ResponseEntity<List<Ads>> getMyAds(@RequestParam String companyId) {
        List<Ads> myAds = adsService.getAdsByCompanyId(companyId);
        return new ResponseEntity<>(myAds, HttpStatus.OK);
    }

    // 광고 등록
    @PostMapping
    public ResponseEntity<Ads> createAd(@RequestBody Ads ad) {
        Ads createdAd = adsService.createAd(ad);
        return new ResponseEntity<>(createdAd, HttpStatus.CREATED);
    }

    // 광고 삭제 (id는 문자열)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable String id) {
        adsService.deleteAd(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
