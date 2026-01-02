package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class LibrarianController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/librarian/dashboard")
    public String dashboard(Model model) {
        User librarian = libraryService.getCurrentUser(); // Must have LIBRARIAN role

        model.addAttribute("user", librarian);
        model.addAttribute("totalBooks", libraryService.getTotalBooks());
        model.addAttribute("pendingBorrowRequests", libraryService.getPendingBorrowRequests());
        model.addAttribute("overdueBooks", libraryService.getOverdueBooks());

        return "librarian/dashboard";
    }
}
