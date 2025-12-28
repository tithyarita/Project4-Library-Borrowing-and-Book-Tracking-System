package com.library.library_borrow_and_book_tracking.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        e.printStackTrace();
        String errorMessage = "An unexpected error occurred: " + (e.getMessage() != null ? e.getMessage() : e.toString());
        model.addAttribute("error", errorMessage);
        model.addAttribute("details", e.getStackTrace());
        return "error";
    }
}
