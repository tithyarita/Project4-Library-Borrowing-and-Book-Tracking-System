package com.library.library_borrow_and_book_tracking.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.service.LibraryService;

@Controller
@RequestMapping("/librarian") // Base URL for all librarian actions
public class LibrarianController {

    @Autowired
    private LibraryService libraryService;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User librarian = libraryService.getCurrentUser();
        model.addAttribute("user", librarian);
        model.addAttribute("totalBooks", libraryService.getTotalBooks());
        model.addAttribute("pendingBorrowRequests", libraryService.getPendingBorrowRequests());
        model.addAttribute("overdueBooks", libraryService.getOverdueBooks());
        return "librarian/dashboard"; // Template: templates/librarian/dashboard.html
    }

    // ================= MANAGE USERS =================
    @GetMapping("/manage_user")
    public String manageUsers(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("user", libraryService.getCurrentUser()); // current logged-in librarian
        model.addAttribute("users", libraryService.searchUsers(q)); // all or filtered users
        model.addAttribute("q", q); // search query
        return "librarian/manage_user"; // Template: templates/librarian/manage_user.html
    }

    @GetMapping("/add-user")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "librarian/add_user"; // Template: templates/librarian/add_user.html
    }

    @PostMapping("/add-user")
    public String saveNewUser(@ModelAttribute("user") User user, RedirectAttributes redirect) {
        try {
            if (user.getActive() == null) user.setActive(true);
            libraryService.saveUser(user);
            redirect.addFlashAttribute("message", "User added successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error adding user: " + e.getMessage());
        }
        return "redirect:/librarian/manage_user";
    }

    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return libraryService.findUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "librarian/edit_user"; // Template: templates/librarian/edit_user.html
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "User not found");
                    return "redirect:/librarian/manage_user";
                });
    }

    @PostMapping("/edit-user/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") User updatedUser,
                             RedirectAttributes redirect) {
        return libraryService.findUserById(id)
                .map(user -> {
                    user.setFullName(updatedUser.getFullName());
                    user.setEmail(updatedUser.getEmail());
                    user.setActive(updatedUser.getActive());
                    libraryService.saveUser(user);
                    redirect.addFlashAttribute("message", "User updated successfully");
                    return "redirect:/librarian/manage_user";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "User not found");
                    return "redirect:/librarian/manage_user";
                });
    }
@PostMapping("/manage-user/deactivate/{id}")
public String deactivateUser(@PathVariable("id") Long id, RedirectAttributes redirect) {
    libraryService.deactivateUser(id);
    redirect.addFlashAttribute("message", "User deactivated successfully");
    return "redirect:/librarian/manage_user";
}

@PostMapping("/manage-user/reactivate/{id}")
public String reactivateUser(@PathVariable("id") Long id, RedirectAttributes redirect) {
    libraryService.reactivateUser(id);
    redirect.addFlashAttribute("message", "User reactivated successfully");
    return "redirect:/librarian/manage_user";
}

    // ================= BORROW MANAGEMENT =================
    @GetMapping("/records")
    public String manageBorrows(Model model) {
        List<BorrowRecord> pendingRecords = libraryService.getAllPendingBorrowRecords();
        model.addAttribute("borrowedItems", pendingRecords);
        return "librarian/manage-borrows"; // Template: templates/librarian/manage-borrows.html
    }

    @PostMapping("/borrow/confirm/{id}")
    public String confirmBorrow(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            libraryService.confirmBorrow(id);
            redirect.addFlashAttribute("success", "Borrow confirmed successfully");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/records";
    }

    // ================= USER HISTORY =================
    @GetMapping("/user/{id}/history")
    public String viewUserHistory(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return libraryService.findUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("history", libraryService.getUserHistory(id));
                    return "librarian/user_history"; // Template: templates/librarian/user_history.html
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "User not found");
                    return "redirect:/librarian/manage_user";
                });
    }
}
