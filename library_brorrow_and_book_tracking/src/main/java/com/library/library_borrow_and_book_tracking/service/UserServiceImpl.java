package com.library.library_borrow_and_book_tracking.service;

import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;
import com.library.library_borrow_and_book_tracking.service.UserService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = (user.getRole() != null && user.getRole().getRoleName() != null)
                    ? user.getRole().getRoleName()
                    : "USER"; // fallback role

        boolean active = user.getActive() != null ? user.getActive() : true;

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .disabled(!active)
                .build();
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    
}
