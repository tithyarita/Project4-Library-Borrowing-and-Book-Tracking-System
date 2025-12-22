package com.library.library_borrow_and_book_tracking.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.RoleRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ NO AuthenticationManager here
    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- PAGES ----------

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        model.addAttribute("userName", authentication.getName());
        return "dashboard";
    }

    // ---------- REGISTER ----------

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        roleRepository.findByRoleName("USER")
                .ifPresent(user::setRole);

        userRepository.save(user);

        return "redirect:/api/auth/login?success";
    }
}
