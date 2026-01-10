package com.library.library_borrow_and_book_tracking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // =====================
    // LIST ALL BOOKS
    // =====================
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // =====================
    // SAVE NEW BOOK
    // =====================
    public void saveBook(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    // =====================
    // GET BOOK BY ID
    // =====================
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // =====================
    // UPDATE BOOK
    // =====================
    public void updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setPublishYear(updatedBook.getPublishYear());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setQuantity(updatedBook.getQuantity());
        existingBook.setAvailable(updatedBook.getAvailable());
        existingBook.setCoverUrl(updatedBook.getCoverUrl());
        existingBook.setUpdatedAt(LocalDateTime.now());

        bookRepository.save(existingBook);
    }

    // =====================
    // DELETE BOOK
    // =====================
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
