package com.example.demo.controller;

import com.example.demo.domain.UploadRecord;
import com.example.demo.service.UploadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final UploadService service;
    public UploadController(UploadService service) { this.service = service; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadRecord upload(
            @RequestParam("product") String productId,
            @RequestPart("file") MultipartFile file,
            HttpSession session) {
        return service.handleUpload(productId, file, session);
    }
}
