package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class BorrowRecordController {

    private final LibraryService libraryService;
    public BorrowRecordController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/user/borrowRecord")
    public String borrowRecord(Model model) {
        model.addAttribute("borrowRecord", libraryService.getRecentBorrows());
        return "user/borrowRecord";
    }

    @GetMapping("/user/add-borrow")
    public String showAddBorrowForm(Model model) {
        try {
            java.util.List<com.library.library_borrow_and_book_tracking.entity.Book> books = libraryService.getFeaturedBooks();
            System.out.println("DEBUG: Books count = " + books.size());
            if (books.isEmpty()) {
                model.addAttribute("warning", "No books available to borrow");
            }
            model.addAttribute("books", books);
        } catch (Exception e) {
            System.out.println("ERROR loading books: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load books: " + e.getMessage());
        }
        return "user/add-borrow";
    }

    @PostMapping("/user/borrowRecord/add")
    public String borrowBook(@RequestParam(name = "bookId", required = false) Long bookId, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (bookId == null || bookId <= 0) {
                model.addAttribute("error", "Please select a book");
                model.addAttribute("books", libraryService.getFeaturedBooks());
                return "user/add-borrow";
            }
            
            libraryService.borrowBook(bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully!");
            return "redirect:/user/borrowRecord";
        } catch (Exception e) {
            e.printStackTrace(); 
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("books", libraryService.getFeaturedBooks());
            return "user/add-borrow";
        }
    }

    @PostMapping("/user/borrowRecord/{id}/return")
    public String returnBook(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/borrowRecord";
    }

    @PostMapping("/user/borrowRecord/{id}/delete")
    public String deleteBorrowRecord(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.deleteBorrowRecord(id);
            redirectAttributes.addFlashAttribute("successMessage", "Borrow record deleted!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/borrowRecord";
    }
}
