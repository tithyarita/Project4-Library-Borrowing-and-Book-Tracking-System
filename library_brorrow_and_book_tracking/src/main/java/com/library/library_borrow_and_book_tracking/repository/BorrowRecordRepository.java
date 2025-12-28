// package com.library.library_borrow_and_book_tracking.repository;

// import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

//     List<BorrowRecord> findByUserIdOrderByBorrowDateDesc(Long userId);
    
//     List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);
    
//     List<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);
    
//     Optional<BorrowRecord> findFirstByUserIdAndBookIdAndReturnDateIsNull(Long userId, Long bookId);
    
//     long countByUserIdAndReturnDateIsNull(Long userId);
    
//     @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE " +
//            "br.user.id = :userId AND " +
//            "br.dueDate < CURRENT_DATE AND " +
//            "br.returnDate IS NULL")
//     long countOverdueBooks(@Param("userId") Long userId);
    
//     @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE " +
//            "br.user.id = :userId AND " +
//            "br.dueDate BETWEEN :startDate AND :endDate AND " +
//            "br.returnDate IS NULL")
//     long countDueSoonBooks(@Param("userId") Long userId, 
//                           @Param("startDate") LocalDate startDate, 
//                           @Param("endDate") LocalDate endDate);
    
//     List<BorrowRecord> findAllByOrderByBorrowDateDesc();
    
//     @Query("SELECT br FROM BorrowRecord br WHERE br.returnDate IS NULL ORDER BY br.dueDate ASC")
//     List<BorrowRecord> findAllActiveBorrows();
// }


package com.library.library_borrow_and_book_tracking.repository;

import com.library.library_borrow_and_book_tracking.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    long countByUserIdAndReturnDateIsNull(Long userId);

    long countByUserIdAndDueDateBeforeAndReturnDateIsNull(Long userId, LocalDate date);

    long countByUserIdAndDueDateBetweenAndReturnDateIsNull(
            Long userId, LocalDate start, LocalDate end
    );

    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    List<BorrowRecord> findByUserIdOrderByBorrowDateDesc(Long userId);

    Optional<BorrowRecord> findFirstByUserIdAndBookIdAndReturnDateIsNull(
            Long userId, Long bookId
    );

    List<BorrowRecord> findAllByOrderByBorrowDateDesc();

        // Add this to replace the missing @Query method
    List<BorrowRecord> findByReturnDateIsNullOrderByDueDateAsc();
}


