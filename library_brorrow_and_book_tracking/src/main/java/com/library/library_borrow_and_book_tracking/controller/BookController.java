package com.library.library_borrow_and_book_tracking.controller;

import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.service.BookService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) throws IOException {

        if (coverFile != null && !coverFile.isEmpty()) {
            String filename = System.currentTimeMillis() + "_" + coverFile.getOriginalFilename();
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads/books");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            coverFile.transferTo(filePath.toFile());
            book.setCoverUrl("/uploads/books/" + filename);
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));
        model.addAttribute("book", book);
        return "update-book";
    }

    @PostMapping("/edit/{id}")
public String updateBook(@PathVariable("id") Long id,
                         @ModelAttribute("book") Book formBook,
                         @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) throws IOException {

    // 1️⃣ Load the existing book from DB
    Book existingBook = bookService.getBookById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));

    // 2️⃣ Update all fields except cover
    existingBook.setTitle(formBook.getTitle());
    existingBook.setAuthor(formBook.getAuthor());
    existingBook.setIsbn(formBook.getIsbn());
    existingBook.setPublisher(formBook.getPublisher());
    existingBook.setPublishYear(formBook.getPublishYear());
    existingBook.setCategory(formBook.getCategory());
    existingBook.setQuantity(formBook.getQuantity());
    existingBook.setAvailable(formBook.getQuantity() != null && formBook.getQuantity() > 0);

    // 3️⃣ Update cover only if a new file is uploaded
    if (coverFile != null && !coverFile.isEmpty()) {
        String filename = System.currentTimeMillis() + "_" + coverFile.getOriginalFilename();
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads/books");
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);
        coverFile.transferTo(filePath.toFile());
        existingBook.setCoverUrl("/uploads/books/" + filename);
    }

    // 4️⃣ Save the updated book
    bookService.saveBook(existingBook);

    return "redirect:/books";
}

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
    
}
