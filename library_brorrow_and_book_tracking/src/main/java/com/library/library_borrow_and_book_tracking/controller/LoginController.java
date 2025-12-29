package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login") // changed from "/home"
    public String loginPage() {
        return "login"; // your login.html template
    }

    @GetMapping("/login/home") // renamed to avoid conflict
    public String loginHome() {
        return "loginHome"; // your login home page template
    }
}
