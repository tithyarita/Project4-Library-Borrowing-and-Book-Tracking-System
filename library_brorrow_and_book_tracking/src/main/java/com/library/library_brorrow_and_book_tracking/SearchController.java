package com.library.library_brorrow_and_book_tracking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @GetMapping("/user/search")
    public String showSearchPage(@RequestParam(required = false) String q, Model model) {
        // Optional: pass the search query back to the page
        model.addAttribute("q", q);
        return "user/search"; // ← This loads: templates/user/search.html
    }
}