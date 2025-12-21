package com.library.library_brorrow_and_book_tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class LibraryService {

    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BorrowRecordRepository borrowRecordRepository;

    // 🔑 Hardcoded user ID (no login)
    private static final Long CURRENT_USER_ID = 1L;

    public User getCurrentUser() {
        return userRepository.findById(CURRENT_USER_ID)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            query, query, query);
    }

    public void borrowBook(Long bookId) {
        User user = getCurrentUser();
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2));
        record.setStatus("BORROWED");
        borrowRecordRepository.save(record);
    }

    public List<BorrowRecord> getRecentBorrows() {
        return borrowRecordRepository.findByUserIdOrderByBorrowDateDesc(CURRENT_USER_ID);
    }

    public long getBorrowedCount() {
        return borrowRecordRepository.findByUserIdAndReturnDateIsNull(CURRENT_USER_ID).size();
    }
}