package com.library.library_borrow_and_book_tracking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserIdOrderByBorrowDateDesc(Long userId);

    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    long countByUserIdAndDueDateBeforeAndReturnDateIsNull(Long userId, LocalDate dueDate);
}
