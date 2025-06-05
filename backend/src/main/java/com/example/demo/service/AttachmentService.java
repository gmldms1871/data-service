package com.example.demo.service;

import com.example.demo.domain.Attachment;
import com.example.demo.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;



@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public Attachment saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;

//        String originalFilename = file.getOriginalFilename();
//        String uuid = UUID.randomUUID().toString();
//        String storedFileName = uuid + "_" + originalFilename;
//        String filePath = uploadDir + storedFileName;
        String originalFilename = file.getOriginalFilename();
        String storedFileName = originalFilename;
        String filePath = uploadDir + storedFileName;

        // 저장
        file.transferTo(new File(filePath));

        Attachment attachment = Attachment.builder()
                .fileName(originalFilename)
                .fileType(file.getContentType())
                .filePath(filePath)
                .createDate(LocalDateTime.now())
                .build();

        return attachmentRepository.save(attachment);
    }

    public List<Attachment> findByIds(List<String> ids) {
        return attachmentRepository.findAllById(ids);
    }
}
