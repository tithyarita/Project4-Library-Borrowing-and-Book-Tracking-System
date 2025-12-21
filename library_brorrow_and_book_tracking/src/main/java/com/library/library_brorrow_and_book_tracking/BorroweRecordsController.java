package com.library.library_brorrow_and_book_tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BorroweRecordsController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/user/borroweRecords")
    public String borroweRecords(Model model) {
        model.addAttribute("borrowRecords", libraryService.getRecentBorrows());
        return "user/borroweRecords";
    }
}