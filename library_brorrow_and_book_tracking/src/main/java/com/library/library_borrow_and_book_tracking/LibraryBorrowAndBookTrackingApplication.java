package com.library.library_borrow_and_book_tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.library.library_borrow_and_book_tracking")
@EnableJpaRepositories("com.library.library_borrow_and_book_tracking.repository")
@EntityScan("com.library.library_borrow_and_book_tracking.entity")
public class LibraryBorrowAndBookTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryBorrowAndBookTrackingApplication.class, args);
    }
}
