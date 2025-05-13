package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ads {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "company_id", nullable = false, length = 100)
    private String companyId;

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @Column(name = "sub_add_id", nullable = false)
    private Long subAddId;

    @Column(name = "ad_image", length = 255)
    private String adImage;
}
