package com.example.demo.controller;

import com.example.demo.domain.Inquiries;
import com.example.demo.service.InquiriesService;
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
    @PostMapping
    public ResponseEntity<Inquiries> createInquiries(@RequestBody Inquiries inquiries) {
        Inquiries saved = inquiriesService.createInquiries(inquiries);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 응답
    }

    // GET /api/inquiries?productId=xxx
    // 특정 상품의 문의 목록을 반환
    @GetMapping
    public ResponseEntity<List<Inquiries>> getInquiries(@RequestParam String productId) {
        List<Inquiries> list = inquiriesService.getInquiriesByProductId(productId);
        return ResponseEntity.ok(list); // 200 OK
    }
}
