package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter @Setter
public class Tag {
    @Id
    @Column(length = 100)
    private String id;

    @Column(length = 100)
    private String name;
}
