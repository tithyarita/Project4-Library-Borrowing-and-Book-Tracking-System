package com.library.library_borrow_and_book_tracking.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BorrowRecordController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/user/borrowRecord")
    public String borrowRecord(Model model) {
        model.addAttribute("borrowRecord", libraryService.getRecentBorrows());
        return "user/borrowRecord";
    }
}