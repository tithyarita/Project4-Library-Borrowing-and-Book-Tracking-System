package com.library.library_brorrow_and_book_tracking;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProcessReturnController {
    @GetMapping("/librarian/process_return")
    public String processReturn() {
        return "librarian/process_return";
    }
}