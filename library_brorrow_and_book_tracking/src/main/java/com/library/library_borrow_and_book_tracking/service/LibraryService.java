package com.library.library_borrow_and_book_tracking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.BookRepository;
import com.library.library_borrow_and_book_tracking.repository.BorrowRecordRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;

@Service
public class LibraryService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public LibraryService(UserRepository userRepository,
                          BookRepository bookRepository,
                          BorrowRecordRepository borrowRecordRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    // ==================== USER OPERATIONS ====================

    public User getCurrentUser() {
        Long currentUserId = getTemporaryUserId();
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Long getTemporaryUserId() {
        // Temporary hardcoded user ID
        return 1L;
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                query, query, query);
    }

    @Transactional
    public BorrowRecord borrowBook(Long bookId) {
        User user = getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailable() == null || !book.getAvailable()) {
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

        return record;
    }

    public List<BorrowRecord> getRecentBorrows() {
        return borrowRecordRepository.findByUserIdOrderByBorrowDateDesc(getTemporaryUserId());
    }

    public long getBorrowedCount() {
        return borrowRecordRepository.findByUserIdAndReturnDateIsNull(getTemporaryUserId()).size();
    }

    public long getOverdueCount() {
        return borrowRecordRepository.countByUserIdAndDueDateBeforeAndReturnDateIsNull(
                getTemporaryUserId(), LocalDate.now());
    }

    public Optional<BorrowRecord> getLatestBorrow() {
        List<BorrowRecord> records = getRecentBorrows();
        return records.isEmpty() ? Optional.empty() : Optional.of(records.get(0));
    }

    // ==================== BOOK OPERATIONS ====================

    public List<Book> getFeaturedBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book addBook(String title, String author, String category,
                        String isbn, String publisher, Integer publishYear, String coverUrl) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setPublishYear(publishYear);
        // book.setCoverUrl(coverUrl != null ? coverUrl : "");
        book.setAvailable(true);

        return bookRepository.save(book);
    }

    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }
}
