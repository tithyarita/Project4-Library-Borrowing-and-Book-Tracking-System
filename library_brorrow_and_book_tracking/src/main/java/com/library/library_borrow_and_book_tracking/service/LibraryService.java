package com.library.library_borrow_and_book_tracking.service;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import com.library.library_borrow_and_book_tracking.entity.User;
import com.library.library_borrow_and_book_tracking.repository.BookRepository;
import com.library.library_borrow_and_book_tracking.repository.BorrowRecordRepository;
import com.library.library_borrow_and_book_tracking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for library operations including borrowing, returning,
 * dashboard statistics, and account management (e.g., blacklisting).
 */
@Service
@Transactional
public class LibraryService {

    private final UserRepository userRepository;  // Required for blacklisting
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserService userService;
  

    /**
     * Constructor-based dependency injection.
     */
    public LibraryService(
            BorrowRecordRepository borrowRecordRepository,
            BookRepository bookRepository,
            UserService userService,
            UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // ================== DASHBOARD & STATISTICS ==================

    /**
     * Returns the number of books currently borrowed by the logged-in user.
     */
    public long getBorrowedCount() {
        User user = userService.getCurrentUserOrThrow();
        return borrowRecordRepository.countByUserIdAndReturnDateIsNull(user.getId());
    }

    /**
     * Returns the number of books overdue (due date is before today).
     */
    public long getOverdueCount() {
        User user = userService.getCurrentUserOrThrow();
        return borrowRecordRepository.countByUserIdAndDueDateBeforeAndReturnDateIsNull(
                user.getId(), LocalDate.now()
        );
    }

    /**
     * Returns the number of books due in the next 3 days (tomorrow to 3 days from now).
     */
    public long getDueSoonCount() {
        User user = userService.getCurrentUserOrThrow();
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        return borrowRecordRepository.countByUserIdAndDueDateBetweenAndReturnDateIsNull(
                user.getId(), start, end
        );
    }

    /**
     * Returns all currently borrowed books (not yet returned).
     */
    public List<BorrowRecord> getCurrentBorrows() {
        User user = userService.getCurrentUserOrThrow();
        return borrowRecordRepository.findByUserIdAndReturnDateIsNull(user.getId());
    }

    /**
     * Returns the full borrow history of the current user, ordered by borrow date (newest first).
     */
    public List<BorrowRecord> getUserBorrowRecords() {
        User user = userService.getCurrentUserOrThrow();
        return borrowRecordRepository.findByUserIdOrderByBorrowDateDesc(user.getId());
    }

    /**
     * Returns the 5 most recent borrow records for dashboard preview.
     */
    public List<BorrowRecord> getRecentBorrows() {
        User user = userService.getCurrentUserOrThrow();
        List<BorrowRecord> records = borrowRecordRepository.findByUserIdOrderByBorrowDateDesc(user.getId());
        return records.stream().limit(5).toList();
    }

    // ================== BLACKLIST MANAGEMENT ==================

    /**
     * Checks if the current user has any book overdue by more than 3 days.
     * If so, blacklists the user account automatically.
     * 
     * This method should be called on every dashboard or sensitive page access.
     */
    // public void checkAndBlacklistOverdueUsers() {
    //     User user = userService.getCurrentUserOrThrow();

    //     // Skip if already blacklisted
    //     if (Boolean.TRUE.equals(user.getBlacklisted())) {
    //         return;
    //     }

    //     // Count books overdue by more than 3 days (due date < today - 3 days)
    //     LocalDate thresholdDate = LocalDate.now().minusDays(3);
    //     long severelyOverdueCount = borrowRecordRepository.countByUserIdAndDueDateBeforeAndReturnDateIsNull(
    //             user.getId(), thresholdDate
    //     );

    //     // Blacklist if at least one book is severely overdue
    //     if (severelyOverdueCount > 0) {
    //         user.setBlacklisted(true);
    //         user.setBlacklistedAt(LocalDateTime.now());
    //         userRepository.save(user);
    //     }
    // }

    /**
     * Restores a blacklisted user account (e.g., after fine payment).
     */
    // public void restoreUserAccount() {
    //     User user = userService.getCurrentUserOrThrow();
    //     if (Boolean.TRUE.equals(user.getBlacklisted())) {
    //         user.setBlacklisted(false);
    //         user.setBlacklistedAt(null);
    //         userRepository.save(user);
    //     }
    // }

    // ================== BORROW / RETURN ==================

    /**
     * Allows a user to borrow a book if available.
     * 
     * @throws RuntimeException if book is not found, not available, or already borrowed.
     */
    public BorrowRecord borrowBook(Long bookId) {
        User user = userService.getCurrentUserOrThrow();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        if (!book.getAvailable()) {
            throw new RuntimeException("Book \"" + book.getTitle() + "\" is not available for borrowing.");
        }

        // Prevent duplicate active borrow
        Optional<BorrowRecord> existingBorrow = borrowRecordRepository
                .findFirstByUserIdAndBookIdAndReturnDateIsNull(user.getId(), bookId);
        if (existingBorrow.isPresent()) {
            throw new RuntimeException("You have already borrowed this book and not returned it yet.");
        }

        // Create borrow record
        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2)); // Standard 2-week loan
        record.setStatus("BORROWED");
        // record.setCreatedAt(LocalDateTime.now());
        // record.setUpdatedAt(LocalDateTime.now());

        // Mark book as unavailable
        book.setAvailable(false);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    /**
     * Returns a borrowed book, making it available again.
     * 
     * @throws RuntimeException if borrow record is not found or already returned.
     */
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found."));

        if (record.getReturnDate() != null) {
            throw new RuntimeException("This book has already been returned.");
        }

        record.setReturnDate(LocalDate.now());
        record.setStatus("RETURNED");
        // record.setUpdatedAt(LocalDateTime.now());

        // Make book available again
        Book book = record.getBook();
        book.setAvailable(true);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    // ================== ADMIN (FUTURE USE) ==================

    /**
     * Returns all borrow records (for librarian/admin view).
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAllByOrderByBorrowDateDesc();
    }
    
}

