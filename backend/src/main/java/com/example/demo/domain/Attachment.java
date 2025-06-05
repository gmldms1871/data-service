package com.example.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "common_attachments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, length = 100, updatable = false)
    private String id; // UUID 자동 생성

    @Column(name = "file_name")
    private String fileName;     // 업로드된 파일명 (원본)

    @Column(name = "file_type")
    private String fileType;     // MIME 타입

    @Column(name = "file_url")
    private String filePath;     // 실제 저장 경로 (file_url 필드에 매핑)

    @Column(name = "created_at")
    private LocalDateTime createDate;

    @Column(name = "updated_at")
    private LocalDateTime updateDate;

    @Column(name = "deleted_at")
    private LocalDateTime deleteDate;
}
