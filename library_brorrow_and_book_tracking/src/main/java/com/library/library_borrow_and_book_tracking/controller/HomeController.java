package com.library.library_borrow_and_book_tracking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/user/home")
    public String home() {
        return "user/home"; 
    }
}
