// package com.library.library_borrow_and_book_tracking.service;

// import com.library.library_borrow_and_book_tracking.entity.Role;
// import com.library.library_borrow_and_book_tracking.entity.User;
// import com.library.library_borrow_and_book_tracking.repository.RoleRepository;
// import com.library.library_borrow_and_book_tracking.repository.UserRepository;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.Optional;

// @Service
// @Transactional
// public class UserService {

//     private final UserRepository userRepository;
//     private final RoleRepository roleRepository;
//     private final PasswordEncoder passwordEncoder;

//     public UserService(UserRepository userRepository, 
//                       RoleRepository roleRepository,
//                       PasswordEncoder passwordEncoder) {
//         this.userRepository = userRepository;
//         this.roleRepository = roleRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     public Optional<User> getCurrentUser() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         if (authentication == null || 
//             !authentication.isAuthenticated() || 
//             "anonymousUser".equals(authentication.getPrincipal())) {
//             return Optional.empty();
//         }

//         try {
//             String email = ((UserDetails) authentication.getPrincipal()).getUsername();
//             return userRepository.findByEmail(email);
//         } catch (ClassCastException e) {
//             // If principal is not UserDetails (e.g., during testing)
//             return Optional.empty();
//         }
//     }

//     public User getCurrentUserOrThrow() {
//         return getCurrentUser()
//                 .orElseThrow(() -> new RuntimeException("No authenticated user found. Please log in."));
//     }

//     public User registerUser(String fullName, String email, String password) {
//         if (userRepository.existsByEmail(email)) {
//             throw new RuntimeException("Email already registered: " + email);
//         }

//         Role userRole = roleRepository.findByRoleName("USER")
//                 .orElseThrow(() -> new RuntimeException("USER role not found"));

//         User user = new User();
//         user.setFullName(fullName);
//         user.setEmail(email);
//         user.setPassword(passwordEncoder.encode(password));
//         user.setRole(userRole);
//         user.setActive(true);
//         user.setCreatedAt(LocalDateTime.now());
//         user.setUpdatedAt(LocalDateTime.now());

//         return userRepository.save(user);
//     }

//     public long getTotalUsers() {
//         return userRepository.countByActiveTrue();
//     }
// }

package com.library.library_borrow_and_book_tracking.service;

import com.library.library_borrow_and_book_tracking.entity.Role;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.RoleRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email);
    }

    public User getCurrentUserOrThrow() {
        return getCurrentUser().orElseThrow(() -> new RuntimeException("User not authenticated"));
    }

    public User registerUser(String fullName, String email, String password) {
        // if (userRepository.existsByEmail(email)) {
        //     throw new RuntimeException("Email already registered");
        // }
        Role userRole = roleRepository.findByRoleName("USER")
            .orElseThrow(() -> new RuntimeException("USER role not found"));
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}