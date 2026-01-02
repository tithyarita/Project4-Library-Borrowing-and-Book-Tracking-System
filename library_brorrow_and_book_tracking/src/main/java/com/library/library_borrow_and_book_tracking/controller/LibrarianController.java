package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
public class LibrarianController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/librarian/dashboard")
    public String dashboard(Model model) {
        User librarian = libraryService.getCurrentUser(); // Must have LIBRARIAN role

        model.addAttribute("user", librarian);
        model.addAttribute("totalBooks", libraryService.getTotalBooks());
        model.addAttribute("pendingBorrowRequests", libraryService.getPendingBorrowRequests());
        model.addAttribute("overdueBooks", libraryService.getOverdueBooks());

        return "librarian/dashboard";
    }
      // ===================== MANAGE USERS =====================
    @GetMapping("librarian/manage_user")
    public String manageUsers(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("user", libraryService.getCurrentUser());
        model.addAttribute("users", libraryService.searchUsers(q));
        model.addAttribute("q", q);
        return "librarian/manage_user"; // Thymeleaf template
    }

    // ===================== DELETE USER =====================
    @PostMapping("/manage-user/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            libraryService.deleteUser(id);
            redirect.addFlashAttribute("message", "User deleted successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/manage-user";
    }
     // ================= EDIT USER FORM =================
    @GetMapping("/librarian/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return libraryService.findUserById(id).map(user -> {
            model.addAttribute("user", user);
            return "librarian/edit_user"; // Thymeleaf template
        }).orElseGet(() -> {
            redirect.addFlashAttribute("error", "User not found");
            return "redirect:/librarian/manage-user";
        });
}
}
