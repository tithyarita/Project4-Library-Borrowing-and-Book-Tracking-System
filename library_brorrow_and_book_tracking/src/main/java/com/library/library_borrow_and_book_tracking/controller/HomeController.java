package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home"; // make sure home.html exists in src/main/resources/templates/
    }
}
