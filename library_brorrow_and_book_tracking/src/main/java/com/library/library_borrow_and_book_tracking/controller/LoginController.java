package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅ Already logged in → go home
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().isEmpty()) {
            return "redirect:/";
        }

        return "login";
    }
}
