// File: src/main/java/com/library/library_borrow_and_book_tracking/LibraryService.java
package com.library.library_borrow_and_book_tracking.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.library_borrow_and_book_tracking.Repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class LibraryService {

    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BorrowRecordRepository borrowRecordRepository;

    private static final Long CURRENT_USER_ID = 1L;

    public User getCurrentUser() {
        return userRepository.findById(CURRENT_USER_ID)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            query, query, query);
    }

    public void borrowBook(Long bookId) {
        User user = getCurrentUser();
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2));
        record.setStatus("BORROWED");
        borrowRecordRepository.save(record);

        book.setAvailable(false);
        bookRepository.save(book);
    }

    public List<BorrowRecord> getRecentBorrows() {
        return borrowRecordRepository.findByUserIdOrderByBorrowDateDesc(CURRENT_USER_ID);
    }

    public long getBorrowedCount() {
        return borrowRecordRepository.findByUserIdAndReturnDateIsNull(CURRENT_USER_ID).size();
    }

    public long getOverdueCount() {
        return borrowRecordRepository.countByUserIdAndDueDateBeforeAndReturnDateIsNull(
            CURRENT_USER_ID, LocalDate.now());
    }

    public BorrowRecord getLatestBorrow() {
        List<BorrowRecord> records = getRecentBorrows();
        return records.isEmpty() ? null : records.get(0);
    }

    // ✅ NEW: Get all books for Home page
    public List<Book> getFeaturedBooks() {
        return bookRepository.findAll();
    }

    // ✅ NEW: Add book (used by librarian)
    public void addBook(String title, String author, String category,
                        String isbn, String coverUrl) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setIsbn(isbn);
        book.setCoverUrl(coverUrl != null ? coverUrl : "");
        book.setAvailable(true);
        bookRepository.save(book);
    }
}