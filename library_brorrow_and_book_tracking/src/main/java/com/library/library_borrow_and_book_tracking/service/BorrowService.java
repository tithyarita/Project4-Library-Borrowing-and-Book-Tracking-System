package com.library.library_borrow_and_book_tracking.service;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import com.library.library_borrow_and_book_tracking.repository.BookRepository;
import com.library.library_borrow_and_book_tracking.repository.BorrowRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         BookRepository bookRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    public void borrowBook(BorrowRecord record) {
        Book book = record.getBook();

        if (!Boolean.TRUE.equals(book.getAvailable())) {
            throw new RuntimeException("Book is not available");
        }

        // mark book as unavailable
        book.setAvailable(false);
        bookRepository.save(book);

        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(7));
        record.setStatus("BORROWED");

        // user remains null
        borrowRecordRepository.save(record);
    }

    public void returnBook(Long borrowId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid borrow ID"));

        record.setReturnDate(LocalDate.now());
        record.setStatus("RETURNED");

        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        borrowRecordRepository.save(record);
    }
}
