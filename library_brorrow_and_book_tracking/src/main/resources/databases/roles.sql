CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL
);

INSERT INTO roles (role_name) VALUES ('USER');
INSERT INTO roles (role_name) VALUES ('LIBRARIAN');
