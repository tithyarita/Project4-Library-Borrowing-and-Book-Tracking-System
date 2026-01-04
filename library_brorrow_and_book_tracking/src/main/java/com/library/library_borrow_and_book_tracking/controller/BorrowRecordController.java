package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
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
public String borrowRecord(Model model, Principal principal) {
    model.addAttribute("borrowRecord", libraryService.getAllBorrowRecords(principal.getName()));
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
        RedirectAttributes redirectAttributes, Principal principal) {

    try {
        libraryService.bookReservation(bookId, principal.getName());

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Book booked successfully. Waiting for confirmation."
        );

        return "redirect:/user/dashboard";

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.getMessage()
        );
        return "redirect:/user/home";
    }
}



    // =====================
    // USER: RETURN BOOK
    // =====================
@PostMapping("/user/borrowRecord/{id}/return")
public String returnBook(@PathVariable("id") Long recordId,
                         RedirectAttributes redirectAttributes) {
    try {
        libraryService.returnBook(recordId);
        redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }
    return "redirect:/user/borrowRecord";
}

    // =====================
    // USER: DELETE RECORD
    // =====================
    @PostMapping("/user/borrowRecord/{id}/delete")
public String deleteBorrowRecord(@PathVariable("id") Long recordId,
                                 RedirectAttributes redirectAttributes) {
    try {
        libraryService.deleteBorrowRecord(recordId);
        redirectAttributes.addFlashAttribute("successMessage", "Borrow record deleted");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }
    return "redirect:/user/borrowRecord";
}

    // =====================
    // LIBRARIAN: CONFIRM BORROW
    // =====================
    @PostMapping("/librarian/borrow-requests/{id}/confirm")
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
