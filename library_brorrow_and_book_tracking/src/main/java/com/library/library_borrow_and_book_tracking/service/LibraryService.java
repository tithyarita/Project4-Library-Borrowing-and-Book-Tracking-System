package com.library.library_borrow_and_book_tracking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // =====================
    // TEMP USER (NO AUTH YET)
    // =====================
    private Long getTemporaryUserId() {
        return 1L; // Replace with real authentication later
    }

    public User getCurrentUser() {
        return userRepository.findById(getTemporaryUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =====================
    // BOOK SEARCH
    // =====================
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                        query, query, query);
    }

    // =====================
    // FEATURED BOOKS
    // =====================
  // =====================
// FEATURED BOOKS
// =====================
public List<Book> getFeaturedBooks() {
    // Fetch latest 8 books for homepage
    return bookRepository.findTop8ByOrderByCreatedAtDesc();
}


    // =====================
    // BORROW & BOOKING
    // =====================
    @Transactional
    public BorrowRecord bookReservation(Long bookId) {
        User user = getCurrentUser();

        // Count bookings/borrows in the last 7 days
        long weeklyCount = borrowRecordRepository.countByUserIdAndStatusInAndCreatedAtBetween(
                user.getId(),
                List.of("BOOKING", "BORROWED"),   // Only active bookings and current borrows
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );

        if (weeklyCount >= 3) {
            throw new RuntimeException("Maximum 3 bookings per week allowed");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!Boolean.TRUE.equals(book.getAvailable())) {
            throw new RuntimeException("Book is not available");
        }

        // Create the booking record
        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setStatus("BOOKING");
        record.setCreatedAt(LocalDateTime.now());

        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord confirmBorrow(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (!"BOOKING".equals(record.getStatus())) {
            throw new RuntimeException("This record is not a booking");
        }

        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2));
        record.setStatus("BORROWED");

        Book book = record.getBook();
        book.setAvailable(false);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
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

        if ("BORROWED".equals(record.getStatus())) {
            Book book = record.getBook();
            book.setAvailable(true);
            bookRepository.save(book);
        }

        borrowRecordRepository.delete(record);
    }

    // =====================
    // DASHBOARD METRICS
    // =====================
    public List<BorrowRecord> getRecentBorrows() {
        User user = getCurrentUser();
        return borrowRecordRepository.findByUserIdAndStatusInOrderByBorrowDateDesc(
                user.getId(),
                List.of("BOOKING", "BORROWED")
        );
    }

    public long getBorrowedCount() {
        return borrowRecordRepository.countByUserIdAndStatus(
                getTemporaryUserId(), "BORROWED"
        );
    }

    public long getOverdueCount() {
        return borrowRecordRepository.countByUserIdAndStatusAndDueDateBefore(
                getTemporaryUserId(), "BORROWED", LocalDate.now()
        );
    }

    public Optional<BorrowRecord> getLatestBorrow() {
    return borrowRecordRepository.findTopByUserIdOrderByBorrowDateDesc(
            getTemporaryUserId()
    );
}

}
