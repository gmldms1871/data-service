package com.example.demo.domain;

import lombok.*;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTagId implements Serializable {
    private String productId;
    private String tagId;
}
