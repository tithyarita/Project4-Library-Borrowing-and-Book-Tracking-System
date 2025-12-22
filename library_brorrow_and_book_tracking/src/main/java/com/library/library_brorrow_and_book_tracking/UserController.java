package com.library.library_brorrow_and_book_tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/user/home")
    public String home() {
        return "user/home";
    }

    @GetMapping("/user/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        List<Book> books = libraryService.searchBooks(q);
        model.addAttribute("books", books);
        return "user/search";
    }

    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", libraryService.getCurrentUser());
        model.addAttribute("recentBorrows", libraryService.getRecentBorrows());
        model.addAttribute("borrowedCount", libraryService.getBorrowedCount());
        model.addAttribute("overdueCount", libraryService.getOverdueCount());
        model.addAttribute("dueSoonCount", 0); // optional: implement later
        model.addAttribute("availableHoldsCount", 0);
        return "user/dashboard";
    }

    @GetMapping("/user/borrowRecord")
    public String borrowRecord(Model model) {
        model.addAttribute("borrowRecord", libraryService.getRecentBorrows());
        return "user/borrowRecord";
    }

    @GetMapping("/user/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId) {
        libraryService.borrowBook(bookId);
        return "redirect:/user/dashboard";
    }

    // Optional: show latest receipt
    @GetMapping("/user/receipt")
    public String receipt(Model model) {
        var record = libraryService.getLatestBorrow();
        if (record == null) {
            return "redirect:/user/dashboard";
        }
        model.addAttribute("receipt", record);
        return "user/receipt";
    }
}