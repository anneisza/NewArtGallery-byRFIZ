// src/main/java/com/galeriseni/galeri_seni/service/ArtistService.java
package com.galeriseni.galeri_seni.service;

import com.galeriseni.galeri_seni.entity.Artist;
import com.galeriseni.galeri_seni.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;

    public List<Artist> findAll() { return artistRepository.findAll(); }

    public Optional<Artist> findById(Long id) { return artistRepository.findById(id); }

    public Artist save(Artist artist) { return artistRepository.save(artist); }

    public Artist create(String name, String specialty, String bio, String photo) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setSpecialty(specialty);
        artist.setBio(bio);
        artist.setPhoto(photo);
        return artistRepository.save(artist);
    }

    public Artist update(Long id, String name, String specialty, String bio, String photo) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seniman tidak ditemukan"));
        artist.setName(name);
        artist.setSpecialty(specialty);
        artist.setBio(bio);
        if (photo != null && !photo.isBlank()) artist.setPhoto(photo);
        return artistRepository.save(artist);
    }

    public void delete(Long id) { artistRepository.deleteById(id); }

    public long countAll() { return artistRepository.count(); }
}