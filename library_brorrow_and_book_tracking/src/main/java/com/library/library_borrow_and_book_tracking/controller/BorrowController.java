package com.library.library_borrow_and_book_tracking.controller;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import com.library.library_borrow_and_book_tracking.service.BookService;
import com.library.library_borrow_and_book_tracking.service.BorrowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    private final BorrowService borrowService;
    private final BookService bookService;

    public BorrowController(BorrowService borrowService, BookService bookService) {
        this.borrowService = borrowService;
        this.bookService = bookService;
    }

    @GetMapping
    public String listBorrowRecords(Model model) {
        model.addAttribute("borrowRecords", borrowService.getAllBorrowRecords());
        return "borrow-list";
    }

    @GetMapping("/add/{bookId}")
    public String showBorrowForm(@PathVariable Long bookId, Model model) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + bookId));

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);

        model.addAttribute("borrowRecord", borrowRecord);
        return "borrow-book";
    }

    @PostMapping("/save")
    public String borrowBook(@ModelAttribute BorrowRecord borrowRecord) {
        borrowService.borrowBook(borrowRecord); // user = null
        return "redirect:/borrow";
    }

    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return "redirect:/borrow";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "BorrowController working!";
    }
}
