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

        // ✅ If user is already logged in, redirect to /home
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().isEmpty()
                && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/home";
        }

        // Show login page for unauthenticated users
        return "login";
    }
}
