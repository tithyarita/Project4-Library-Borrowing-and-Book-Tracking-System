package com.library.library_brorrow_and_book_tracking;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ManageBookController {
    @GetMapping("/librarian/manage_book")
    public String manageBooks() {
        return "librarian/manage_book"; // Note: you'll need to make this dynamic later
    }
}

