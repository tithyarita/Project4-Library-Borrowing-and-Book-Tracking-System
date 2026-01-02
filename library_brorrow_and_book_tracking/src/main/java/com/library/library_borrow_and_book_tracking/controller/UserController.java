package com.library.library_borrow_and_book_tracking.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class UserController {

    private final LibraryService libraryService;

    // ✅ Constructor injection
    public UserController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // ===== HOME PAGE =====
    @GetMapping("/user/home")
    public String home(Model model) {
        model.addAttribute("books", libraryService.getFeaturedBooks());
        return "home";
    }

    // ===== SEARCH BOOKS =====
    @GetMapping("/user/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        List<Book> books = libraryService.searchBooks(q);
        model.addAttribute("books", books);
        return "user/search";
    }

    // ===== DASHBOARD =====
    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", libraryService.getCurrentUser());
        model.addAttribute("recentBorrows", libraryService.getRecentBorrows());
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount());
        model.addAttribute("overdueCount", libraryService.getOverdueCount());
        // Placeholder values; update later if needed
        model.addAttribute("dueSoonCount", 0);
        model.addAttribute("availableHoldsCount", 0);
        return "user/dashboard";
    }

    // ===== BORROW RECORDS =====
    @GetMapping("/user/borrowRecords")
    public String borrowRecords(Model model) {
        model.addAttribute("borrowRecords", libraryService.getRecentBorrows());
        return "user/borrowRecords";
    }

    // ===== BOOK (RESERVATION) =====
  @GetMapping("/user/booking/{bookId}")
public String book(@PathVariable("bookId") Long bookId, RedirectAttributes redirect) {
    try {
        libraryService.bookReservation(bookId);
        redirect.addFlashAttribute("message", "Booking created. Please confirm at the library.");
        return "redirect:/user/receipt"; // handled by ReceiptController
    } catch (RuntimeException e) {
        redirect.addFlashAttribute("error", e.getMessage());
        return "redirect:/user/home";
    }
}


    // NOTE: Removed `/user/receipt` from here to avoid conflict with ReceiptController
}
