package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;
import com.library.library_borrow_and_book_tracking.service.UserService;

@Controller
public class LibrarianController {

    @Autowired
    private LibraryService libraryService;
    @Autowired
    private UserService userService;

    @GetMapping("/librarian/dashboard")
    public String dashboard(Model model, Principal principal) {
        User librarian = userService.findByEmail(principal.getName()); // Must have LIBRARIAN role

        model.addAttribute("user", librarian);
        model.addAttribute("totalBooks", libraryService.getTotalBooks());
        model.addAttribute("pendingBorrowRequests", libraryService.getPendingBorrowRequests());
        model.addAttribute("overdueBooks", libraryService.getOverdueBooks());

        return "librarian/dashboard";
    }

    @GetMapping("/librarian/records")
    public String manageBorrows(Model model) {
        model.addAttribute(
            "borrowedItems",
            libraryService.getAllPendingBorrowRecords()
        );
        return "librarian/manage-borrows";
    }

    @PostMapping("/librarian/borrow/confirm/{id}")
    public String confirmBorrow(@PathVariable("id") Long recordId, RedirectAttributes redirectAttributes) {
        try {
            libraryService.confirmBorrow(recordId);
            redirectAttributes.addFlashAttribute("success", "Borrow confirmed successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/records";
    }
}
