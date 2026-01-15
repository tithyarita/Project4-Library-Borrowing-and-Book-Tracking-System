package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import com.library.library_borrow_and_book_tracking.entity.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;
import com.library.library_borrow_and_book_tracking.service.UserService;

@Controller
public class UserController {

    private final LibraryService libraryService;
    private final UserService userService;

    // ✅ Constructor injection
    public UserController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    // ===== WELCOME PAGE =====
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // ===== HOME PAGE =====
    @GetMapping("/user/home")
    public String home(Model model) {
        List<Book> books = libraryService.getFeaturedBooks();
        Map<String, List<Book>> booksByCategory = books.stream()
                .collect(Collectors.groupingBy(Book::getCategory));
        model.addAttribute("booksByCategory", booksByCategory);
        return "home";
    }


    // ===== SEARCH BOOKS =====
    @GetMapping("/user/search")
    public String search(@RequestParam(name = "q", required = false) String q,
                         @RequestParam(name = "category", required = false) String category,
                         Model model) {
        List<Book> books = libraryService.searchBooks(q, category);
        model.addAttribute("books", books);
        return "user/search";
    }


    // ===== BORROW RECORDS =====
    // @GetMapping("/user/borrowRecords")
    // public String borrowRecords(Model model, Principal principal) {
    //     model.addAttribute("borrowRecords", libraryService.getRecentBorrows(principal.getName()));
    //     return "user/borrowRecords";
    // }

    // ===== BOOK (RESERVATION) =====
    @GetMapping("/user/booking/{bookId}")
        public String book(@PathVariable("bookId") Long bookId, RedirectAttributes redirect, Principal principal) {
            try {
                libraryService.bookReservation(bookId, principal.getName());
                redirect.addFlashAttribute("message", "Booking created. Please confirm at the library.");
                return "redirect:/user/receipt"; // handled by ReceiptController
            } catch (RuntimeException e) {
                redirect.addFlashAttribute("error", e.getMessage());
                return "redirect:/user/home";
            }
    }

    // ===== USER ACCOUNT =====
    @GetMapping("/user/account")
    public String account(Model model, Principal principal) {
        // Load current user by email (Principal holds the username/email)
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user/account";
    }

    // ===== EDIT PROFILE =====
    @GetMapping("/user/account/edit")
    public String editProfileForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user/edit_profile";
    }

    // ===== UPDATE PROFILE =====
    @PostMapping("/user/account/edit")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("email") String email,
                                Principal principal,
                                RedirectAttributes redirect) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByEmail(principal.getName());
            user.setFullName(fullName);
            user.setEmail(email);
            libraryService.saveUser(user);
            redirect.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/user/account";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/account/edit";
        }
    }

    // ===== UPDATE PASSWORD =====
    @GetMapping("/user/change_password")
    public String changePasswordForm(Model model, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user/change_password";
    }

    @PostMapping({"/user/account/change-password", "/user/change_password"})
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 RedirectAttributes redirect) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login";
        }

        try {
            libraryService.changeUserPassword(principal.getName(), currentPassword, newPassword);
            redirect.addFlashAttribute("successMessage", "Password changed successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/account";
    }

    @GetMapping("/user/borrowRecords")
    public String borrowRecords(Model model) {
        model.addAttribute("borrowRecords", libraryService.getRecentBorrows(null));
        return "user/borrowRecords";
    }


    // @PostMapping("/user/borrowRecord/{bookId}")
    // public String borrowBook(@PathVariable("bookId") Long bookId,
    //                         Principal principal,
    //                         RedirectAttributes redirectAttributes) {
    //     libraryService.bookReservation(bookId, principal.getName());
    //     redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully!");
    //     return "redirect:/user/dashboard";
    // }

  


    


}
