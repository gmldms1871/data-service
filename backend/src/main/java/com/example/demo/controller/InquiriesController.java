package com.example.demo.controller;

import com.example.demo.domain.Inquiries;
import com.example.demo.service.InquiriesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
public class InquiriesController {

    @Autowired
    private InquiriesService inquiriesService;

    // POST /api/inquiries
    // 클라이언트로부터 문의 내용을 받아 생성
    // todo 프론트(https param)에서 제품 id 받아와서 작성하게 //null 허용 제외
    // todo attachment는  업로드 버튼 클릭시 hhtp에 저장된
    // 1. 문의 하기에 첨부 파일 등록
    // 2. bean에 임시 저장?
    // 3.

    @PostMapping("create")
    public ResponseEntity<?> createInquiries(@RequestBody Inquiries inquiries, HttpSession session) {

        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        inquiries.setCompanyId(companyId);
//        inquiries.setAttachmentId(inquiries.getAttachmentId());

        try {
            Inquiries saved = inquiriesService.createInquiries(inquiries);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 응답
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // GET /api/inquiries?productId=xxx
    // 특정 상품의 문의 목록을 반환
    @GetMapping("/product/readMany")
    public ResponseEntity<?> getInquiriesProductId(@RequestParam String productId, HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
//        System.out.println("ddd"+ companyId + "  " + productId);
        List<Inquiries> list = inquiriesService.findByCompanyIdAndProductId(companyId, productId);
        return ResponseEntity.ok(list); // 200 OK
    }

    // GET /api/inquiries?companyId=xxx
    // 문의한 기업의 문의 목록을 반환
    @GetMapping("/readMany")
    public ResponseEntity<?> getInquiriesCompanyId(HttpSession session) {
        String companyId = (String) session.getAttribute("loginCompanyId");
        if (companyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<Inquiries> list = inquiriesService.getInquiriesByCompanyId(companyId);
        return ResponseEntity.ok(list); // 200 OK
    }
}

// @RequestParam String companyId,

// 제약 조건 걸어서 2개중 하나만