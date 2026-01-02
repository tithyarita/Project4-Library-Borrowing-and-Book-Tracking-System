package com.library.library_borrow_and_book_tracking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path /uploads/books/** to folder uploads/books in project root
        registry.addResourceHandler("/uploads/books/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/books/");
    }
}
