package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import com.library.library_borrow_and_book_tracking.entity.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;
import com.library.library_borrow_and_book_tracking.service.UserService;

@Controller
public class UserController {

    private final LibraryService libraryService;
    private final UserService userService;

    // ✅ Constructor injection
    public UserController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    // ===== HOME PAGE =====
    @GetMapping("/user/home")
    public String home(Model model) {
        model.addAttribute("books", libraryService.getFeaturedBooks());
        return "home";
    }

    // ===== SEARCH BOOKS =====
   @GetMapping("/user/search")
    public String search(@RequestParam(name = "q", required = false) String q, Model model) {
    List<Book> books = libraryService.searchBooks(q);
    model.addAttribute("books", books);
    return "user/search";
    }

    // ===== BORROW RECORDS =====
    @GetMapping("/user/borrowRecords")
    public String borrowRecords(Model model, Principal principal) {
        model.addAttribute("borrowRecords", libraryService.getRecentBorrows(principal.getName()));
        return "user/borrowRecords";
    }

    // ===== BOOK (RESERVATION) =====
  @GetMapping("/user/booking/{bookId}")
public String book(@PathVariable("bookId") Long bookId, RedirectAttributes redirect, Principal principal) {
    try {
        libraryService.bookReservation(bookId, principal.getName());
        redirect.addFlashAttribute("message", "Booking created. Please confirm at the library.");
        return "redirect:/user/receipt"; // handled by ReceiptController
    } catch (RuntimeException e) {
        redirect.addFlashAttribute("error", e.getMessage());
        return "redirect:/user/home";
    }
}

}
