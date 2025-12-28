package com.library.library_borrow_and_book_tracking.controller;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.BookService;
import com.library.library_borrow_and_book_tracking.service.LibraryService;
import com.library.library_borrow_and_book_tracking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final BookService bookService;
    private final LibraryService libraryService;

    public UserController(UserService userService, BookService bookService, LibraryService libraryService) {
        this.userService = userService;
        this.bookService = bookService;
        this.libraryService = libraryService;
    }

    // ================== PROTECTED PAGES (require blacklist check) ==================

    @GetMapping("/home")
    public String home(Model model) {
        // if (isUserBlacklisted()) {
        //     return "redirect:/user/account-blacklisted";
        // }
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        return "user/home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // if (isUserBlacklisted()) {
        //     return "redirect:/user/account-blacklisted";
        // }
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount());
        model.addAttribute("overdueCount", libraryService.getOverdueCount());
        model.addAttribute("recentBorrows", libraryService.getCurrentBorrows());
        return "user/dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        // if (isUserBlacklisted()) {
        //     return "redirect:/user/account-blacklisted";
        // }
        model.addAttribute("books", bookService.searchBooks(q));
        model.addAttribute("query", q);
        return "user/search";
    }

    @GetMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId) {
        // if (isUserBlacklisted()) {
        //     return "redirect:/user/account-blacklisted";
        // }
        libraryService.borrowBook(bookId);
        return "redirect:/user/dashboard?success";
    }

    @GetMapping("/borrowRecords")
    public String borrowRecords(Model model) {
        // if (isUserBlacklisted()) {
        //     return "redirect:/user/account-blacklisted";
        // }
        model.addAttribute("borrowRecords", libraryService.getUserBorrowRecords());
        return "user/borrowRecord";
    }

    @GetMapping("/account")
    public String account(Model model) {
        User user = userService.getCurrentUser().orElse(null);
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        // Allow blacklisted users to view account (to see fine & pay)
        model.addAttribute("user", user);
        return "user/account";
    }

    // ================== BLACKLIST PAYMENT ==================

    @GetMapping("/account-blacklisted")
    public String accountBlacklisted(Model model) {
        User user = userService.getCurrentUser().orElse(null);
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        // Even if not blacklisted, redirect to dashboard
        // if (!Boolean.TRUE.equals(user.getBlacklisted())) {
        //     return "redirect:/user/dashboard";
        // }
        model.addAttribute("user", user);
        return "user/account-blacklisted";
    }

    // @PostMapping("/pay-fine")
    // public String payFine() {
    //     User user = userService.getCurrentUser().orElse(null);
    //     if (user != null && Boolean.TRUE.equals(user.getBlacklisted())) {
    //         libraryService.restoreUserAccount(); // Restore account
    //     }
    //     return "redirect:/user/dashboard?restored";
    // }

    // ================== HELPER METHOD ==================

    /**
     * Checks if the current user is blacklisted.
     * Also auto-blacklists if they have books overdue by more than 3 days.
     * @return true if user is blacklisted
     */
    // private boolean isUserBlacklisted() {
    //     try {
    //         // This will auto-blacklist if overdue > 3 days
    //         libraryService.checkAndBlacklistOverdueUsers();
    //         User user = userService.getCurrentUserOrThrow();
    //         return Boolean.TRUE.equals(user.getBlacklisted());
    //     } catch (Exception e) {
    //         // Not logged in or error → redirect to login
    //         return true; // Will trigger redirect
    //     }
    // }
}