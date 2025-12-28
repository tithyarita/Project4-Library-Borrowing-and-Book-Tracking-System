
// package com.library.library_borrow_and_book_tracking.service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.library.library_borrow_and_book_tracking.entity.Book;
// import com.library.library_borrow_and_book_tracking.repository.BookRepository;

// @Service
// @Transactional
// public class BookService {

//     private final BookRepository bookRepository;

//     public BookService(BookRepository bookRepository) {
//         this.bookRepository = bookRepository;
//     }

//     public List<Book> getAllBooks() {
//         return bookRepository.findAll();
//     }

//     public List<Book> getAvailableBooks() {
//         return bookRepository.findByAvailableTrue();
//     }

//     public List<Book> searchBooks(String query) {
//         if (query == null || query.trim().isEmpty()) {
//             return getAvailableBooks();
//         }
//         return bookRepository.searchBooks(query.trim());
//     }

//     public List<Book> getFeaturedBooks() {
//         List<Book> availableBooks = getAvailableBooks();
//         return availableBooks.stream()
//                 .limit(12)
//                 .toList();
//     }

//     public Optional<Book> getBookById(Long id) {
//         return bookRepository.findById(id);
//     }

//     public Book saveBook(Book book) {
//         if (book.getId() == null) {
//             book.setCreatedAt(LocalDateTime.now());
//         }
//         book.setUpdatedAt(LocalDateTime.now());
//         if (book.getAvailable() == null) {
//             book.setAvailable(true);
//         }
//         return bookRepository.save(book);
//     }

//     public void deleteBook(Long id) {
//         bookRepository.deleteById(id);
//     }

//     public long getTotalBooks() {
//         return bookRepository.count();
//     }

//     public long getAvailableBooksCount() {
//         return bookRepository.countByAvailableTrue();
//     }
// }

package com.library.library_borrow_and_book_tracking.service;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() { return bookRepository.findAll(); }
    public List<Book> getAvailableBooks() { return bookRepository.findByAvailableTrue(); }
    public List<Book> getFeaturedBooks() { return getAvailableBooks().stream().limit(12).toList(); }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) return getAvailableBooks();
        return bookRepository.searchBooks(query.trim());
    }

    public Optional<Book> getBookById(Long id) { return bookRepository.findById(id); }

    public Book saveBook(Book book) {
        if (book.getId() == null) book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        if (book.getAvailable() == null) book.setAvailable(true);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) { bookRepository.deleteById(id); }
    public long getAvailableBooksCount() { return bookRepository.countByAvailableTrue(); }
}