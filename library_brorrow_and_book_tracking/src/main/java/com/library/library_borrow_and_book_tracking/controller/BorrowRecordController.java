package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class BorrowRecordController {

    private final LibraryService libraryService;

    // ✅ Constructor injection (recommended)
    public BorrowRecordController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/user/borrowRecord")
    public String borrowRecord(Model model) {
        model.addAttribute("borrowRecord", libraryService.getRecentBorrows());
        return "user/borrowRecord";
    }
}
