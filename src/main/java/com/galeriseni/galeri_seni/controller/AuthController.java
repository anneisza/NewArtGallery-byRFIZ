// src/main/java/com/galeriseni/galeri_seni/controller/AuthController.java
package com.galeriseni.galeri_seni.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}



