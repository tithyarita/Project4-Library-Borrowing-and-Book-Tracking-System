CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(50),
    publisher VARCHAR(100),
    publish_year INT,
    category VARCHAR(50),
    available BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
