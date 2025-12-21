package com.library.library_borrow_and_book_tracking.repository;

import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
}
