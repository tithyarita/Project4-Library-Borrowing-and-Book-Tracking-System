package com.library.library_borrow_and_book_tracking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final int MAX_BORROW_LIMIT = 3;

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

    // ===================== CURRENT USER =====================
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    // ===================== USER MANAGEMENT =====================
    public List<User> searchUsers(String query) {
        if (query == null || query.isEmpty()) return userRepository.findAll();
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
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

    // ===================== BOOK MANAGEMENT =====================
    public List<Book> getFeaturedBooks() {
        return bookRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.isEmpty()) return List.of();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query, query);
    }

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    // ===================== BORROW MANAGEMENT =====================
 @Transactional
public BorrowRecord bookReservation(Long bookId, String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // ✅ ENFORCE MAX 3 BOOKS
    if (getActiveBorrowCount(user.getId()) >= MAX_BORROW_LIMIT) {
        throw new RuntimeException("You can only borrow up to 3 books at one time");
    }

    Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

    if (!book.getAvailable()) {
        throw new RuntimeException("Book is not available");
    }

    BorrowRecord record = new BorrowRecord();
    record.setUser(user);
    record.setBook(book);
    record.setIsbn(book.getIsbn());
    record.setStatus("BOOKED");
    record.setCreatedAt(LocalDateTime.now());

    // decrement quantity for reservation
    Integer currentQty = book.getQuantity() == null ? 0 : book.getQuantity();
    book.setQuantity(currentQty - 1);
    book.setAvailable(book.getQuantity() != null && book.getQuantity() > 0);
    bookRepository.save(book);

    return borrowRecordRepository.save(record);
}


       @Transactional
    public void confirmBorrow(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        record.setStatus("BORROWED");
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));

        borrowRecordRepository.save(record);
    }

    @Transactional
    public void returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);
        record.setStatus(today.isAfter(record.getDueDate()) ? "RETURNED_LATE" : "RETURNED_ON_TIME");

        Book book = record.getBook();
        if (book != null) {
            Integer q = book.getQuantity();
            book.setQuantity((q == null ? 0 : q) + 1);
            book.setAvailable(book.getQuantity() != null && book.getQuantity() > 0);
            bookRepository.save(book);
        }

        borrowRecordRepository.save(record);
    }

    @Transactional
    public void deleteBorrowRecord(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        Book book = record.getBook();
        if (book != null) {
            // Return quantity if record was not returned
            Integer q = book.getQuantity();
            book.setQuantity((q == null ? 0 : q) + 1);
            book.setAvailable(book.getQuantity() != null && book.getQuantity() > 0);
            bookRepository.save(book);
        }

        borrowRecordRepository.delete(record);
    }

    // ===================== USER BORROW HISTORY =====================
    @Transactional(readOnly = true)
    public List<BorrowRecord> getRecentBorrows(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.findByUserIdAndStatusInOrderByCreatedAtDesc(user.getId(), List.of("BOOKED", "BORROWED"));
    }

    public long getBookedCount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.countByUserIdAndStatus(user.getId(), "BOOKED");
    }

    public long getBorrowedCount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.countByUserIdAndStatus(user.getId(), "BORROWED");
    }

    public long getDueSoonCount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate today = LocalDate.now();
        return borrowRecordRepository.countByUserIdAndStatusAndDueDateBetween(user.getId(), "BORROWED", today, today.plusDays(3));
    }

    public long getOverdueCount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.countByUserIdAndStatusAndDueDateBefore(user.getId(), "BORROWED", LocalDate.now());
    }

    public List<BorrowRecord> getUserHistory(Long userId) {
        return borrowRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ===================== DASHBOARD METRICS =====================
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


    
    public List<BorrowRecord> getAllBorrowRecords(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    return borrowRecordRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
}
@Transactional(readOnly = true)
public List<BorrowRecord> getAllBorrowRecordsForLibrarian() {
    return borrowRecordRepository.findAllByOrderByCreatedAtDesc();
}


@Transactional
public void requestReturn(Long recordId) {
    BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));

    if (!"BORROWED".equals(record.getStatus())) {
        throw new RuntimeException("Only borrowed books can be returned");
    }

    record.setStatus("RETURN_REQUESTED");
    borrowRecordRepository.save(record);
}
@Transactional
public void confirmReturn(Long recordId) {
    BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));

    if (!"BORROWED".equals(record.getStatus())) {
        throw new RuntimeException("Only borrowed books can be returned");
    }

    LocalDate today = LocalDate.now();
    record.setReturnDate(today);

    // Status: on time or late
    if (today.isAfter(record.getDueDate())) {
        record.setStatus("RETURNED_LATE");
    } else {
        record.setStatus("RETURNED_ON_TIME");
    }

    Book book = record.getBook();
    if (book != null) {
        book.setAvailable(true);
        bookRepository.save(book);
    }

    borrowRecordRepository.save(record);
}

private int getActiveBorrowCount(Long userId) {
    long booked = borrowRecordRepository.countByUserIdAndStatus(userId, "BOOKED");
    long borrowed = borrowRecordRepository.countByUserIdAndStatus(userId, "BORROWED");
    return (int) (booked + borrowed);
}
@Transactional(readOnly = true)
public List<BorrowRecord> searchBorrowRecords(String query) {
    if (query == null || query.trim().isEmpty()) {
        return borrowRecordRepository.findAllByOrderByCreatedAtDesc();
    }
    return borrowRecordRepository.searchBorrowRecords(query.trim());
}


  
}
