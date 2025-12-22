CREATE TABLE borrow_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    borrow_date DATE,
    due_date DATE,
    return_date DATE,
    status VARCHAR(50),
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);
