package com.library.library_borrow_and_book_tracking.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.library.library_borrow_and_book_tracking.entity.Role;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.RoleRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        // Check email uniqueness
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        user.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Assign default USER role
        Optional<Role> roleOptional = roleRepository.findByRoleName("USER");
        Role role;
        if (roleOptional.isPresent()) {
            role = roleOptional.get();
        } else {
            // Create USER role if it doesn't exist
            role = new Role();
            role.setRoleName("USER");
            role = roleRepository.save(role);
        }
        user.setRole(role);

        // Save user to database
        userRepository.save(user);

        return "redirect:/api/auth/login?success";
    }
}
