package com.library.library_borrow_and_book_tracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publishYear;
    private String category;
    private Boolean available;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // getters & setters
}
