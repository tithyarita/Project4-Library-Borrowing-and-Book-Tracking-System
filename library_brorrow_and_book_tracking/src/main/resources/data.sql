-- ==================== INSERT ROLES ====================
INSERT INTO roles (id, role_name) VALUES (1, 'ADMIN') ON DUPLICATE KEY UPDATE role_name='ADMIN';
INSERT INTO roles (id, role_name) VALUES (2, 'USER') ON DUPLICATE KEY UPDATE role_name='USER';
INSERT INTO roles (id, role_name) VALUES (3, 'LIBRARIAN') ON DUPLICATE KEY UPDATE role_name='LIBRARIAN';

-- ==================== INSERT USERS ====================
INSERT INTO users (id, full_name, email, password, active, role_id, created_at, updated_at) 
VALUES (1, 'John Doe', 'john@example.com', 'password123', TRUE, 2, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE full_name='John Doe';

INSERT INTO users (id, full_name, email, password, active, role_id, created_at, updated_at) 
VALUES (2, 'Jane Smith', 'jane@example.com', 'password456', TRUE, 2, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE full_name='Jane Smith';

INSERT INTO users (id, full_name, email, password, active, role_id, created_at, updated_at) 
VALUES (3, 'Admin User', 'admin@example.com', 'admin123', TRUE, 1, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE full_name='Admin User';

-- ==================== INSERT BOOKS ====================
INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (1, 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner', 1925, 'Fiction', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='The Great Gatsby';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (2, 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'Lippincott', 1960, 'Fiction', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='To Kill a Mockingbird';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (3, '1984', 'George Orwell', '9780451524935', 'Penguin', 1949, 'Dystopian', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='1984';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (4, 'Pride and Prejudice', 'Jane Austen', '9780141439518', 'Penguin', 1813, 'Romance', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='Pride and Prejudice';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (5, 'The Catcher in the Rye', 'J.D. Salinger', '9780316769174', 'Little Brown', 1951, 'Fiction', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='The Catcher in the Rye';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (6, 'Brave New World', 'Aldous Huxley', '9780060085261', 'Harper Perennial', 1932, 'Dystopian', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='Brave New World';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (7, 'Jane Eyre', 'Charlotte Bronte', '9780141441146', 'Penguin', 1847, 'Romance', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='Jane Eyre';

INSERT INTO books (id, title, author, isbn, publisher, publish_year, category, available, created_at, updated_at) 
VALUES (8, 'The Hobbit', 'J.R.R. Tolkien', '9780547928227', 'Houghton Mifflin', 1937, 'Fantasy', TRUE, NOW(), NOW()) 
ON DUPLICATE KEY UPDATE title='The Hobbit';
