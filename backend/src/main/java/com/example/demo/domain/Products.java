package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {

    @Id
    private String id;

    @Column(name = "company_id", length = 100)
    private String companyId;

    @Column(name = "category_id", length = 100)
    private String categoryId;

    @Column(name = "main_image", length = 255)
    private String mainImage;

    @Column(name = "description_image", length = 255)
    private String descriptionImage;

    @Column(name = "extension_list", columnDefinition = "TEXT")
    private String extensionList;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "name", length = 100)  // nullable = false 제거
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "attachment_id", length = 100)
    private String attachmentId;

    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
