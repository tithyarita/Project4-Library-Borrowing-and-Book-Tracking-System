
package com.library.library_borrow_and_book_tracking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrow")
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Long bookId;

    private LocalDate borrowDate;

    private boolean returned = false;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}

