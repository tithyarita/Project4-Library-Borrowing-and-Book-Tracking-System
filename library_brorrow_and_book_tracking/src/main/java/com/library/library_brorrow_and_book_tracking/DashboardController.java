package com.library.library_brorrow_and_book_tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", libraryService.getCurrentUser());
        model.addAttribute("recentBorrows", libraryService.getRecentBorrows());
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount());
        return "user/dashboard";
    }
}