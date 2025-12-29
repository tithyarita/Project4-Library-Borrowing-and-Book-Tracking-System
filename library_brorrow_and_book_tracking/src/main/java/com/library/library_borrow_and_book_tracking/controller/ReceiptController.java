package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class ReceiptController {

    private final LibraryService libraryService;

    // ✅ Constructor injection (recommended)
    public ReceiptController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // @GetMapping("/user/receipt")
    public String receipt(Model model) {

        var records = libraryService.getRecentBorrows();

        if (records.isEmpty()) {
            model.addAttribute("message", "No recent borrowings.");
            return "redirect:/user/dashboard";
        }

        model.addAttribute("receipt", records.get(0));
        return "user/receipt";
    }
}
