package com.library.library_borrow_and_book_tracking.controller;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;
import com.library.library_borrow_and_book_tracking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final LibraryService libraryService;
    private final UserService userService;

    public DashboardController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {
        // Get current user
        User user = userService.getCurrentUser()
                .orElse(null);

        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // ✅ Check and auto-blacklist if overdue > 3 days
        // libraryService.checkAndBlacklistOverdueUsers();

        // ✅ If blacklisted, redirect to payment page
        // if (Boolean.TRUE.equals(user.getBlacklisted())) {
        //     return "redirect:/user/account-blacklisted";
        // }

        // Normal dashboard data
        model.addAttribute("user", user);
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount());
        model.addAttribute("dueSoonCount", libraryService.getDueSoonCount());
        model.addAttribute("overdueCount", libraryService.getOverdueCount());
        model.addAttribute("recentBorrows", libraryService.getRecentBorrows());

        return "user/dashboard";
    }
}