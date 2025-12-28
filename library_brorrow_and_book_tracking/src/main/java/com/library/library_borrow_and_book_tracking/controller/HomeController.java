package com.library.library_borrow_and_book_tracking.controller;

import com.library.library_borrow_and_book_tracking.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    // Public homepage
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        return "index";
    }

    // User homepage (after login)
    @GetMapping("/user/home")
    public String userHome(Model model) {
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        return "user/home";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}

