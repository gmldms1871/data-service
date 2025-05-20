package com.example.demo.service;

import com.example.demo.domain.Inquiries;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.InquiriesRepository;
import com.example.demo.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class InquiriesService {

    @Autowired
    private InquiriesRepository inquiriesRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ProductsRepository productsRepository;

    // 문의 생성 및 DB 저장
    public Inquiries createInquiries(Inquiries inquiries) {
        // 외부에서 이 메서드를 호출해도 항상 유효성 검사가 적용됨
        return createInquiriesWithValidation(inquiries);
    }

    //
    public Inquiries createInquiriesWithValidation(Inquiries inquiries) {
        boolean companyExists = false;
        boolean productExists = false;

        // companyId 유효성 검사
        if (inquiries.getCompanyId() != null && !inquiries.getCompanyId().isBlank()) {
            companyExists = companyRepository.existsById(inquiries.getCompanyId());
        }

        // productId 유효성 검사
        if (inquiries.getProductId() != null && !inquiries.getProductId().isBlank()) {
            productExists = productsRepository.existsById(inquiries.getProductId());
        }

        if (!companyExists && !productExists) {
            throw new IllegalArgumentException("companyId 또는 productId가 유효하지 않습니다.");
        }

        // 나머지 기본 처리
        if (inquiries.getAttachmentId() == null || inquiries.getAttachmentId().isEmpty()) {
            inquiries.setAttachmentId(null);
        }

        if (inquiries.getEditAddDate() == null) {
            inquiries.setEditAddDate(LocalDateTime.now());
        }

        return inquiriesRepository.save(inquiries);
    }


    // 상품별 문의 목록 조회
    public List<Inquiries> getInquiriesByProductId(String productId) {
        return inquiriesRepository.findByProductId(productId);
    }

    public List<Inquiries> getInquiriesByCompanyId(String companyId) {
        return inquiriesRepository.findByCompanyId(companyId);
    }

    // 위와 동일 하므로 수정 시 확인
    public List<Inquiries> findByCompanyId(String companyId) {
        return inquiriesRepository.findByCompanyId(companyId);
    }

    public List<Inquiries> findByCompanyIdAndProductId(String companyId, String productId) {
        return inquiriesRepository.findByCompanyIdAndProductId(companyId, productId);
    }
}


// attachmentId가 없으면 UUID 생성하여 attachmentId에 할당
//        if (inquiries.getAttachmentId() == null || inquiries.getAttachmentId().isEmpty()) {
//        inquiries.setAttachmentId(null);  // attachmentId가 없으면 null로 설정
//        }
//
//                // editAddDate 설정
//                if (inquiries.getEditAddDate() == null) {
//        inquiries.setEditAddDate(LocalDateTime.now());
//        }
//
//        return inquiriesRepository.save(inquiries);

