package com.library.library_brorrow_and_book_tracking;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManageUserController {
    @GetMapping("/librarian/manage_user")
    public String manageUser() {
        return "librarian/manage_user";
    }
}
