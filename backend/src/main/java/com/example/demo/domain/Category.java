package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "name", length = 100)
    private String name;
}
