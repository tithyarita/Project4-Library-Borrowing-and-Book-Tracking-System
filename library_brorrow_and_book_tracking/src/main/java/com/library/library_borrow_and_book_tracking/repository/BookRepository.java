// BookRepository.java
package com.library.library_borrow_and_book_tracking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.library.library_borrow_and_book_tracking.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Get latest 8 books for featured section
    List<Book> findTop8ByOrderByCreatedAtDesc();

    // Search books by title, author, or category
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String title,
            String author,
            String category
    );
}
