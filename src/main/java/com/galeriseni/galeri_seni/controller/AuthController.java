// src/main/java/com/galeriseni/galeri_seni/controller/AuthController.java
package com.galeriseni.galeri_seni.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/hash")
    @ResponseBody
    public String hash(@RequestParam String pw) {
        return new BCryptPasswordEncoder(10).encode(pw);
    }

    @Autowired
    private com.galeriseni.galeri_seni.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/fix-password")
    @ResponseBody
    public String fixPassword() {
        userRepository.findByEmail("admin@artgallery.art").ifPresent(u -> {
            u.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(u);
        });
        userRepository.findByEmail("Rain@artgallery.art").ifPresent(u -> {
            u.setPassword(passwordEncoder.encode("kurator123"));
            userRepository.save(u);
        });
        return "Password berhasil diupdate!";
    }
}





