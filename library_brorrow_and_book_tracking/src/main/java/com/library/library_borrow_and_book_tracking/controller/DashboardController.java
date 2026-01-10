package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;
import com.library.library_borrow_and_book_tracking.service.UserService;

@Controller
public class DashboardController {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserService userService;
    @GetMapping("/user/dashboard")
    public String dashboard(Model model, Principal principal) {
    if (principal == null || principal.getName() == null) {
        return "redirect:/login";
    }

    String email = principal.getName();
    User user;
    try {
        user = userService.findByEmail(email);
    } catch (Exception e) {
        model.addAttribute("error", "User not found for email: " + email);
        return "error";
    }

    try {
        model.addAttribute("user", user);
        model.addAttribute("recentBorrows", libraryService.getRecentBorrows(email));
        model.addAttribute("bookedCount", libraryService.getBookedCount(email));
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount(email));
        model.addAttribute("dueSoonCount", libraryService.getDueSoonCount(email));
        model.addAttribute("overdueCount", libraryService.getOverdueCount(email));
        // Provide count for available holds used by the template
        model.addAttribute("availableHoldsCount", libraryService.getBookedCount(email));

        return "user/dashboard";
    } catch (Exception e) {
        // Avoid bubbling unexpected exceptions to container — show friendly error
        model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Unexpected error");
        model.addAttribute("details", true);
        return "error";
    }
}


}