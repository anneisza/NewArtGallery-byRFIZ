// src/main/java/com/galeriseni/galeri_seni/entity/Artist.java
package com.galeriseni.galeri_seni.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String specialty;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo")
    private String photo;

    @Column(name = "total_works")
    private Integer totalWorks = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "artist")
    private List<Artwork> artworks;
}