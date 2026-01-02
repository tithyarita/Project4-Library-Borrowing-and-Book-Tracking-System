package com.library.library_borrow_and_book_tracking.controller;

import com.library.library_borrow_and_book_tracking.controller.BookController;
import com.library.library_borrow_and_book_tracking.entity.Book;
import com.library.library_borrow_and_book_tracking.service.BookService;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/books")
public class BookController {
    
    private final BookService bookService;

    // ✅ Constructor injection (THIS WAS MISSING)
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
                       @RequestParam("coverFile") MultipartFile coverFile) throws IOException {

    if (!coverFile.isEmpty()) {
        String filename = System.currentTimeMillis() + "_" + coverFile.getOriginalFilename();

        // ✅ Use project root folder
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads/books");
        Files.createDirectories(uploadPath); // create folder if missing

        Path filePath = uploadPath.resolve(filename);
        coverFile.transferTo(filePath.toFile());

        // Save URL relative to server for displaying in HTML
        book.setCoverUrl("/uploads/books/" + filename);
    }

    bookService.saveBook(book);
    return "redirect:/books";
}


    @GetMapping("/edit/{id}")
public String showEditForm(@PathVariable Long id, Model model) {
    Book book = bookService.getBookById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));
    model.addAttribute("book", book);
    return "update-book";
}

@GetMapping("/delete/{id}")
public String deleteBook(@PathVariable Long id) {
    bookService.deleteBook(id);
    return "redirect:/books";
}


    @GetMapping("/test")
@ResponseBody
public String test() {
    return "BookController working!";
}

   
    
}