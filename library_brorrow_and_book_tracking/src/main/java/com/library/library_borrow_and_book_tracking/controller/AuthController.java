package com.library.library_borrow_and_book_tracking.controller;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller; // Changed from RestController
import org.springframework.ui.Model; // Added missing import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.RoleRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;

@Controller // Changed to Controller to support HTML templates
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, 
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    // --- GET METHODS (Display HTML Pages) ---

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Points to src/main/resources/templates/register.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Points to src/main/resources/templates/login.html
    }

    // --- POST METHODS (Handle Form Submission) ---

    @PostMapping("/register")
    public String register(@ModelAttribute User user) { // Changed to @ModelAttribute for HTML forms
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        
        // Assign default USER role
        roleRepository.findByRoleName("USER").ifPresent(user::setRole);
        
        userRepository.save(user);
        return "redirect:/api/auth/login"; // Redirects to login page after success
    }
    @GetMapping("/dashboard")
public String showDashboard() {
    return "dashboard"; // Points to dashboard.html
}

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            return "redirect:/dashboard"; // Redirect to a dashboard/home page on success
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password");
            return "login"; // Stay on login page if it fails
        }
    }
}