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
import java.util.Optional;


@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public Attachment saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;

        String originalFilename = file.getOriginalFilename();
        String storedFileName = originalFilename;

        // 디렉토리 경로 정리
        String uploadPath = uploadDir.endsWith(File.separator)
                ? uploadDir
                : uploadDir + File.separator;

        // 전체 저장 경로 조합 (OS에 맞게 안전하게)
        String filePath = uploadPath + storedFileName;

        // 디렉토리 없으면 생성 (파일 경로 X)
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 저장
        file.transferTo(new File(filePath));

        // DB 저장용 객체 생성
        Attachment attachment = Attachment.builder()
                .fileName(originalFilename)
                .fileType(file.getContentType())
                .filePath(filePath)
                .createDate(LocalDateTime.now())
                .build();

        return attachmentRepository.save(attachment);
    }


    public Attachment findById(String id) {
        Optional<Attachment> attachment = attachmentRepository.findById(id);
        if (attachment.isPresent()) return attachment.get();
        else throw new RuntimeException("Attachment not found");
    }
}
