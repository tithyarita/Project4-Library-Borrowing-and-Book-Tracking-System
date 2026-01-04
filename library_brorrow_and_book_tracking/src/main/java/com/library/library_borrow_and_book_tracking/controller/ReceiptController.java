package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class ReceiptController {

    private final LibraryService libraryService;

    // ✅ Constructor injection (best practice)
    public ReceiptController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

   @GetMapping("/user/receipt")
public String receipt(Model model, Principal principal) {
    libraryService.getLatestBorrow(principal.getName()).ifPresentOrElse(
        record -> model.addAttribute("receipt", record),
        () -> model.addAttribute("message", "No recent borrowings.")
    );
    return "user/receipt";
}

}
