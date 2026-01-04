package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.library.library_borrow_and_book_tracking.service.UserService;

@Controller
public class UserController {

    private final LibraryService libraryService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // ✅ Constructor injection
    public UserController(LibraryService libraryService, UserService userService, PasswordEncoder passwordEncoder) {
        this.libraryService = libraryService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
    // @GetMapping("/user/borrowRecords")
    // public String borrowRecords(Model model, Principal principal) {
    //     model.addAttribute("borrowRecords", libraryService.getRecentBorrows(principal.getName()));
    //     return "user/borrowRecords";
    // }

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

    // ===== USER ACCOUNT =====
    @GetMapping("/user/account")
    public String account(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user/account";
    }

    @GetMapping("/user/account/edit")
    public String editAccountForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user/edit_profile";
    }


    @PostMapping("/user/account/edit")
    public String saveAccountChanges(@RequestParam("fullName") String fullName,
                                     @RequestParam("email") String email,
                                     Principal principal,
                                     RedirectAttributes redirect) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userService.findByEmail(principal.getName());
            user.setFullName(fullName);
            user.setEmail(email);
            libraryService.saveUser(user);
            redirect.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Unable to update profile: " + e.getMessage());
        }
        return "redirect:/user/account";
    }

    // ===== CHANGE PASSWORD =====
    @GetMapping("/user/change_password")
    public String changePasswordForm(Principal principal) {
        if (principal == null) return "redirect:/login";
        return "user/change_password";
    }

    @PostMapping("/user/change_password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 RedirectAttributes redirect) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userService.findByEmail(principal.getName());
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirect.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/user/change_password";
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            libraryService.saveUser(user);
            redirect.addFlashAttribute("message", "Password updated successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Unable to change password: " + e.getMessage());
        }
        return "redirect:/user/account";
    }


}
