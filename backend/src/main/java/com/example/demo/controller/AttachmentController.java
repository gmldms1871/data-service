package com.example.demo.controller;

import com.example.demo.domain.Attachment;
import com.example.demo.service.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/attachment")
@Slf4j
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    // 첨부파일 실제 업로드 처리
    @PostMapping("/create")
    public Attachment upload(@RequestParam("file") MultipartFile file) throws IOException {
        return attachmentService.saveFile(file);
    }

    @GetMapping("/download/{idx}")
    public void downloadBoardFile(@PathVariable("idx") String idx, HttpServletResponse response) {
        try {
            Attachment attachment = attachmentService.findById(idx);
            byte[] files = FileUtils.readFileToByteArray(new File(attachment.getFilePath()));

            String mimeType = Files.probeContentType(new File(attachment.getFilePath()).toPath());

            response.setContentType(mimeType == null ? "application/octet-stream" : mimeType);
            response.setContentLength(files.length);
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8) + "\";");
            response.setHeader("Content-Transfer-Encoding", "binary");

            response.getOutputStream().write(files);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.getStackTrace();
        }
    }
}

