package com.library.library_borrow_and_book_tracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.library_borrow_and_book_tracking.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String title,
            String author,
            String category
    );
}
