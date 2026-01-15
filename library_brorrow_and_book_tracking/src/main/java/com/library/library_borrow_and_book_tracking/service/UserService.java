package com.library.library_borrow_and_book_tracking.service;

import com.library.library_borrow_and_book_tracking.entity.User;

public interface UserService {
    User findByEmail(String email);

    
}
