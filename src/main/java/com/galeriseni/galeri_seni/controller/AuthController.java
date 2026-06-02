// src/main/java/com/galeriseni/galeri_seni/controller/AuthController.java
package com.galeriseni.galeri_seni.controller;

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
}





