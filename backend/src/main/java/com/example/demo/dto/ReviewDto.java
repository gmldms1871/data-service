package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {
    private String id;
    private String productId;
    private String companyId;
    private String transactionId;
    private float rating;
    private String review;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "ReviewDto{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", rating=" + rating +
                ", review='" + review + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
