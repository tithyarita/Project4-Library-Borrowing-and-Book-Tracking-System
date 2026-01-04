package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
// Deactivate user
// Deactivate user
@PostMapping("/librarian/manage-user/deactivate/{id}")
public String deactivateUser(@PathVariable("id") Long id, RedirectAttributes redirect) {
    libraryService.deactivateUser(id);
    redirect.addFlashAttribute("message", "User deactivated successfully");
    return "redirect:/librarian/manage_user";
}

// Reactivate user
@PostMapping("/librarian/manage-user/reactivate/{id}")
public String reactivateUser(@PathVariable("id") Long id, RedirectAttributes redirect) {
    libraryService.reactivateUser(id);
    redirect.addFlashAttribute("message", "User reactivated successfully");
    return "redirect:/librarian/manage_user";
}


     // ================= EDIT USER FORM =================
   @GetMapping("/librarian/edit-user/{id}")
public String editUserForm(
        @PathVariable("id") Long id,
        Model model,
        RedirectAttributes redirect) {

    return libraryService.findUserById(id).map(user -> {
        model.addAttribute("user", user);
        return "librarian/edit_user";
    }).orElseGet(() -> {
        redirect.addFlashAttribute("error", "User not found");
        return "redirect:/librarian/manage_user";
    });
}
@PostMapping("/librarian/edit-user/{id}")
public String updateUser(
        @PathVariable("id") Long id,
        @ModelAttribute("user") User formUser,
        RedirectAttributes redirect) {

    return libraryService.findUserById(id).map(user -> {

        // update fields from form
        user.setFullName(formUser.getFullName());
        user.setEmail(formUser.getEmail());
        user.setActive(formUser.getActive());

        libraryService.saveUser(user); // SAVE TO MYSQL ✅

        redirect.addFlashAttribute("message", "User updated successfully");
        return "redirect:/librarian/manage_user";

    }).orElseGet(() -> {
        redirect.addFlashAttribute("error", "User not found");
        return "redirect:/librarian/manage_user";
    });
}
    // ===================== ADD USER FORM =====================
@GetMapping("/librarian/add-user")
public String addUserForm(Model model) {
    model.addAttribute("user", new User()); // Empty User object for the form
    return "librarian/add_user"; // Thymeleaf template
}

// ===================== SAVE NEW USER =====================
@PostMapping("/librarian/add-user")
public String saveNewUser(@ModelAttribute("user") User user, RedirectAttributes redirect) {
    try {
        // Default to active = true
        if (user.getActive() == null) {
            user.setActive(true);
        }
        libraryService.saveUser(user); // Save user to MySQL
        redirect.addFlashAttribute("message", "User added successfully");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", "Error adding user: " + e.getMessage());
    }
    return "redirect:/librarian/manage_user";
}
   @GetMapping("/librarian/user/{id}/history")
public String viewUserHistory(@PathVariable Long id, Model model) {
    User user = libraryService.findUserById(id).orElse(null); // get user safely
    // List<BorrowRecord> history = libraryService.getBorrowHistoryByUser(id);

    model.addAttribute("user", user);
    // model.addAttribute("history", history);

    return "librarian/user_history";
}





}
