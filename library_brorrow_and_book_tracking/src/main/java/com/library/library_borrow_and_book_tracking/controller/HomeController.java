package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        // Redirect to /user/home which already loads books
        return "redirect:/user/home";
    }
}
