package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
 
    @GetMapping("/")
    public String home() {
        return "index"; // this loads index.html
    }
}
