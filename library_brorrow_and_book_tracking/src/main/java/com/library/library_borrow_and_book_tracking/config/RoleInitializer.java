package com.library.library_borrow_and_book_tracking.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.library.library_borrow_and_book_tracking.entity.Role;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.RoleRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;

@Configuration
public class RoleInitializer {

    @Bean
    CommandLineRunner initRolesAndLibrarian(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {

            // --- Create roles if missing ---
            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseGet(() -> roleRepository.save(new Role("USER")));

            Role librarianRole = roleRepository.findByRoleName("LIBRARIAN")
                    .orElseGet(() -> roleRepository.save(new Role("LIBRARIAN")));

            // --- Create default librarian account if not exists ---
            if (userRepository.findByEmail("librarian@library.com").isEmpty()) {
                User librarian = new User();
                librarian.setFullName("Default Librarian");
                librarian.setEmail("librarian@library.com");

                // Encrypt password
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                librarian.setPassword(encoder.encode("librarian123"));

                librarian.setRole(librarianRole);
                librarian.setActive(true);
                librarian.setCreatedAt(LocalDateTime.now());
                librarian.setUpdatedAt(LocalDateTime.now());

                userRepository.save(librarian);
            }
        };
    }
}
