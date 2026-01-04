package com.library.library_borrow_and_book_tracking.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // Recent borrows for a user
    List<BorrowRecord> findByUserIdAndStatusInOrderByBorrowDateDesc(Long userId, List<String> statuses);

    // Count borrowed books
    long countByUserIdAndStatus(Long userId, String status);

    // Count overdue books
    long countByUserIdAndStatusAndDueDateBefore(Long userId, String status, LocalDate date);

    // Count books due soon
    long countByUserIdAndStatusAndDueDateBetween(Long userId, String status, LocalDate start, LocalDate end);

    // Count bookings in last 7 days
    long countByUserIdAndStatusInAndCreatedAtBetween(Long userId, List<String> statuses, LocalDateTime start, LocalDateTime end);

    // Latest borrow
    BorrowRecord findTopByUserIdOrderByBorrowDateDesc(Long userId);

    // Librarian dashboard metrics
    long countByStatus(String status);
    long countByStatusAndDueDateBefore(String status, LocalDate date);
}

