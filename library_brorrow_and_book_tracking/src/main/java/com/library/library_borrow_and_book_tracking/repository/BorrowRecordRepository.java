package com.library.library_borrow_and_book_tracking.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // Count bookings/borrows by user and multiple statuses in a date range
    long countByUserIdAndStatusInAndCreatedAtBetween(Long userId, List<String> statuses, LocalDateTime start, LocalDateTime end);

    // Find the latest borrow record for a user
    Optional<BorrowRecord> findTopByUserIdOrderByBorrowDateDesc(Long userId);

    // List all borrow records for a user with multiple statuses, ordered by borrow date
    List<BorrowRecord> findByUserIdAndStatusInOrderByBorrowDateDesc(Long userId, List<String> statuses);

    // Count borrowed books (single status)
    long countByUserIdAndStatus(Long userId, String status);

    // Count overdue borrowed books
    long countByUserIdAndStatusAndDueDateBefore(Long userId, String status, LocalDate date);
}
