package com.example.demo.domain;

import jakarta.persistence.*;
        import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

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
}
