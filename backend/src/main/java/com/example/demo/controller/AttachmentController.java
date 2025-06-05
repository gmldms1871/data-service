package com.example.demo.controller;

import com.example.demo.domain.Attachment;
import com.example.demo.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    // 첨부파일 실제 업로드 처리
    @PostMapping("/upload")
    public Attachment upload(@RequestParam("file") MultipartFile file) throws IOException {
        return attachmentService.saveFile(file);
    }

    // 파일 ID 목록으로 첨부파일 조회
    @GetMapping("/find")
    public List<Attachment> findByIds(@RequestParam List<String> attachments) {
        return attachmentService.findByIds(attachments);
    }
}

