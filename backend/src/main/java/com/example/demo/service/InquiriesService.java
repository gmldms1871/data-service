package com.example.demo.service;

import com.example.demo.domain.Inquiries;
import com.example.demo.repository.InquiriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InquiriesService {

    @Autowired
    private InquiriesRepository inquiriesRepository;

    // 문의 생성 및 DB 저장
    public Inquiries createInquiries(Inquiries inquiries) {
        // id가 없으면 UUID를 생성하여 id에 할당
        if (inquiries.getId() == null || inquiries.getId().isEmpty()) {
            inquiries.setId(UUID.randomUUID().toString());  // id에 UUID 할당
        }

        // attachmentId가 없으면 UUID 생성하여 attachmentId에 할당
        if (inquiries.getAttachmentId() == null || inquiries.getAttachmentId().isEmpty()) {
            inquiries.setAttachmentId(null);  // attachmentId가 없으면 null로 설정
        }

        // editAddDate 설정
        if (inquiries.getEditAddDate() == null) {
            inquiries.setEditAddDate(LocalDateTime.now());
        }

        return inquiriesRepository.save(inquiries);
    }

    // 상품별 문의 목록 조회
    public List<Inquiries> getInquiriesByProductId(String productId) {
        return inquiriesRepository.findByProductId(productId);
    }
}
