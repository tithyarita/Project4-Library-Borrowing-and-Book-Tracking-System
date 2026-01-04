package com.library.library_borrow_and_book_tracking.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    long countByUserIdAndBorrowDateAfter(Long userId, LocalDate date);

    List<BorrowRecord> findByUserIdAndStatusInOrderByBorrowDateDesc(Long userId, List<String> statuses);

    long countByUserIdAndStatus(Long userId, String status);

    long countByUserIdAndStatusAndDueDateBefore(Long userId, String status, LocalDate date);

    long countByUserIdAndStatusAndDueDateBetween(Long userId, String status, LocalDate start, LocalDate end);

    long countByUserIdAndStatusInAndCreatedAtBetween(Long userId, List<String> statuses, LocalDateTime start, LocalDateTime end);

    BorrowRecord findTopByUserIdOrderByBorrowDateDesc(Long userId);

    long countByStatus(String status);
    long countByStatusAndDueDateBefore(String status, LocalDate date);
    List<BorrowRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    // ✅ Use @Param for named parameter
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = :status")
    List<BorrowRecord> findByStatus(@Param("status") String status);
    List<BorrowRecord> findByUserIdAndStatusInOrderByCreatedAtDesc(
        Long userId,
        List<String> statuses
);
List<BorrowRecord> findAllByOrderByCreatedAtDesc();
   @Query("""
    SELECT br FROM BorrowRecord br
    JOIN br.book b
    JOIN br.user u
    WHERE
        LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
""")
List<BorrowRecord> searchBorrowRecords(@Param("query") String query);

}

