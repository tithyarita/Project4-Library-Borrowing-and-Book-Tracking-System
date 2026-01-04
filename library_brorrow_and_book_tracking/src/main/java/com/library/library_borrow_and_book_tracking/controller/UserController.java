package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.User;
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
    public String search(@RequestParam(name = "q", required = false) String q, Model model) {
    List<Book> books = libraryService.searchBooks(q);
    model.addAttribute("books", books);
    return "user/search";
    }


    // ===== DASHBOARD =====
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        User user = libraryService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount());
        model.addAttribute("dueSoonCount", libraryService.getDueSoonCount());
        model.addAttribute("availableHoldsCount", libraryService.getAvailableHoldsCount());
        model.addAttribute("overdueCount", libraryService.getOverdueCount());
        model.addAttribute("recentBorrows", libraryService.getRecentBorrows());
        return "user/dashboard";
    }

    @GetMapping("/user/account")
    public String account(Model model, Principal principal) {
    User user = libraryService.getCurrentUser();
    model.addAttribute("user", user);
    return "user/account";
    }

    // ===== EDIT PROFILE =====
    @GetMapping("/user/account/edit")
    public String editProfile(Model model) {
        User user = libraryService.getCurrentUser();
        model.addAttribute("user", user);
        return "user/edit_profile";
    }

    @PostMapping("/user/account/edit")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String email,
            RedirectAttributes redirect
    ) {
        User user = libraryService.getCurrentUser();
        user.setFullName(fullName);
        user.setEmail(email);
        libraryService.saveUser(user);

        redirect.addFlashAttribute("success", "Profile updated successfully");
        return "redirect:/user/account";
    }

    // ===== CHANGE PASSWORD =====
    @GetMapping("/user/change_password")
    public String changePasswordPage() {
        return "user/change_password";
    }

    @PostMapping("/user/change_password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirect
    ) {
        User user = libraryService.getCurrentUser();

        if (!libraryService.checkPassword(user, currentPassword)) {
            redirect.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/user/change_password";
        }

        libraryService.updatePassword(user, newPassword);
        redirect.addFlashAttribute("success", "Password updated successfully");
        return "redirect:/user/account";
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
