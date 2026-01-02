package com.library.library_borrow_and_book_tracking.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class BorrowRecordController {

    private final LibraryService libraryService;

    // ✅ Constructor injection
    public BorrowRecordController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // =====================
    // USER: VIEW BORROW RECORDS
    // =====================
    @GetMapping("/user/borrowRecord")
    public String borrowRecord(Model model) {
        model.addAttribute("borrowRecords", libraryService.getRecentBorrows());
        return "user/borrowRecord";
    }

    // =====================
    // USER: SHOW ADD BORROW (BOOKING)
    // =====================
    @GetMapping("/user/add-borrow")
    public String showAddBorrowForm(Model model) {
        List<Book> books = libraryService.getFeaturedBooks();

        if (books.isEmpty()) {
            model.addAttribute("warning", "No books available to borrow");
        }

        model.addAttribute("books", books);
        return "user/add-borrow";
    }

    // =====================
    // USER: CREATE BOOKING
    // =====================
    @PostMapping("/user/borrowRecord/add")
    public String createBooking(
            @RequestParam("bookId") Long bookId,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            libraryService.bookReservation(bookId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book booked successfully. Waiting for confirmation."
            );
            return "redirect:/user/borrowRecord";

        } catch (Exception e) {
            // Return to form with error and current book list
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("books", libraryService.getFeaturedBooks());
            return "user/add-borrow";
        }
    }

    // =====================
    // USER: RETURN BOOK
    // =====================
    @PostMapping("/user/borrowRecord/{id}/return")
    public String returnBook(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            libraryService.returnBook(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book returned successfully"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }
        return "redirect:/user/borrowRecord";
    }

    // =====================
    // USER: DELETE RECORD
    // =====================
    @PostMapping("/user/borrowRecord/{id}/delete")
    public String deleteBorrowRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            libraryService.deleteBorrowRecord(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Borrow record deleted"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }
        return "redirect:/user/borrowRecord";
    }

    // =====================
    // LIBRARIAN: CONFIRM BORROW
    // =====================
    @GetMapping("/librarian/confirm/{id}")
    public String confirmBorrow(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            libraryService.confirmBorrow(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Borrow confirmed"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }
        return "redirect:/librarian/records";
    }
}
