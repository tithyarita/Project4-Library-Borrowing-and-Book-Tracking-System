package com.library.library_borrow_and_book_tracking.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReceiptController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/user/receipt")
    public String receipt(Model model) {
        var records = libraryService.getRecentBorrows();
        if (records.isEmpty()) {
            model.addAttribute("message", "No recent borrowings.");
            return "redirect:/user/dashboard";
        }
        model.addAttribute("receipt", records.get(0));
        return "user/receipt";
    }
}