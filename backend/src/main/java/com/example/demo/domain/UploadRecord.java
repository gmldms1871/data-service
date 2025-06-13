package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UploadRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Enumerated(EnumType.STRING)
    private Badge badge;

    @Column(name = "missing_rate")
    private double missingRate;

    @Column(name = "duplication_rate")
    private double duplicationRate;

    @Column(name = "outlier_rate")
    private double outlierRate;

    @Column(name = "product_id", length = 100, nullable = false)
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Products product;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    public enum Badge { Raw, Cleaned }
}
