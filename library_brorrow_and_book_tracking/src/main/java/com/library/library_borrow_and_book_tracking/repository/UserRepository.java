package com.library.library_borrow_and_book_tracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.library_borrow_and_book_tracking.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // This allows the AuthController to check if an email exists during registration
    Optional<User> findByEmail(String email);

    // Useful for the Library System to find a user by their full name
    Optional<User> findByFullName(String fullName);
}