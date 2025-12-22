package com.library.library_borrow_and_book_tracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.library_borrow_and_book_tracking.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
