package com.library.library_borrow_and_book_tracking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public LibraryService(UserRepository userRepository,
                          BookRepository bookRepository,
                          BorrowRecordRepository borrowRecordRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================
    // USER MANAGEMENT
    // =====================

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user); // Save new or updated user
    }

    @Transactional
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    @Transactional
    public void reactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }

    // @Transactional
    // public void deleteUser(Long id) {
    //     User user = userRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
        
    //     // Delete borrow records first
    //     borrowRecordRepository.deleteByUserId(user.getId());
        
    //     userRepository.delete(user);
    // }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    // =====================
    // BOOK MANAGEMENT
    // =====================

    public List<Book> getFeaturedBooks() {
        return bookRepository.findTop8ByOrderByCreatedAtDesc();
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.isEmpty()) return List.of();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                query, query, query);
    }

    // =====================
    // BORROW & RETURN MANAGEMENT
    // =====================

    @Transactional
    public BorrowRecord bookReservation(Long bookId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long weeklyCount = borrowRecordRepository.countByUserIdAndStatusInAndCreatedAtBetween(
                user.getId(),
                List.of("BOOKED", "BORROWED"),
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );

        if (weeklyCount >= 3) {
            throw new RuntimeException("Maximum 3 bookings per week allowed");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getAvailable()) throw new RuntimeException("Book is not available");

        // Lock the book
        book.setAvailable(false);
        bookRepository.save(book);

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setStatus("BOOKED");
        record.setCreatedAt(LocalDateTime.now());

        return borrowRecordRepository.save(record);
    }

    @Transactional
    public void confirmBorrow(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (!"BOOKED".equals(record.getStatus()))
            throw new RuntimeException("This record is not a booking");

        record.setStatus("BORROWED");
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));

        Book book = record.getBook();
        book.setAvailable(false);

        bookRepository.save(book);
        borrowRecordRepository.save(record);
    }

    @Transactional
    public void returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);

        if (today.isAfter(record.getDueDate())) {
            record.setStatus("RETURNED_LATE");
        } else {
            record.setStatus("RETURNED_ON_TIME");
        }

        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        borrowRecordRepository.save(record);
    }

    @Transactional
    public void deleteBorrowRecord(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        Book book = record.getBook();
        if (book != null) {
            book.setAvailable(true);
            bookRepository.save(book);
        }

        borrowRecordRepository.delete(record);
    }

    // =====================
    // BORROW RECORDS / USER HISTORY
    // =====================

    @Transactional(readOnly = true)
    public List<BorrowRecord> getUserHistory(Long userId) {
        return borrowRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<BorrowRecord> getRecentBorrows(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return borrowRecordRepository.findByUserIdAndStatusInOrderByCreatedAtDesc(
                user.getId(),
                List.of("BOOKED", "BORROWED")
        );
    }

    // =====================
    // DASHBOARD METRICS
    // =====================

    // For librarian
    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getPendingBorrowRequests() {
        return borrowRecordRepository.countByStatus("BOOKED");
    }

    public long getOverdueBooks() {
        return borrowRecordRepository.countByStatusAndDueDateBefore("BORROWED", LocalDate.now());
    }

    public List<BorrowRecord> getAllPendingBorrowRecords() {
        return borrowRecordRepository.findByStatus("BOOKED");
    }

    // For user
    @Transactional(readOnly = true)
    public long getBorrowedCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.countByUserIdAndStatus(user.getId(), "BORROWED");
    }

    @Transactional(readOnly = true)
    public long getOverdueCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.countByUserIdAndStatusAndDueDateBefore(user.getId(), "BORROWED", LocalDate.now());
    }

    @Transactional(readOnly = true)
    public long getDueSoonCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate today = LocalDate.now();
        LocalDate soon = today.plusDays(3);
        return borrowRecordRepository.countByUserIdAndStatusAndDueDateBetween(user.getId(), "BORROWED", today, soon);
    }

    @Transactional(readOnly = true)
    public long getAvailableHoldsCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.countByUserIdAndStatus(user.getId(), "HOLD_AVAILABLE");
    }

    // =====================
    // PASSWORD MANAGEMENT
    // =====================

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
