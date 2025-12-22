package com.library.library_brorrow_and_book_tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserIdOrderByBorrowDateDesc(Long userId);
    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);
    long countByUserIdAndDueDateBeforeAndReturnDateIsNull(Long userId, LocalDate dueDate);
}