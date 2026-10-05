-- ============================================================
-- Project 5 - Login Web Application
-- Database + table creation script
--
-- Run with:  mysql -u root < schema.sql
-- (or paste into the MySQL shell)
-- ============================================================

CREATE DATABASE IF NOT EXISTS project5_login
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE project5_login;

-- Dedicated application user (least privilege: only this DB, no admin rights).
-- Change the password below before using in anything beyond local dev.
CREATE USER IF NOT EXISTS 'project5_app'@'localhost' IDENTIFIED BY 'CHANGE_ME';
GRANT SELECT, INSERT, UPDATE, DELETE ON project5_login.* TO 'project5_app'@'localhost';
FLUSH PRIVILEGES;

-- Main users table
CREATE TABLE IF NOT EXISTS users (
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    user_id                VARCHAR(50)  NOT NULL UNIQUE,
    password_hash          VARCHAR(255) NOT NULL,
    password_salt          VARCHAR(255) NOT NULL,
    first_name             VARCHAR(100) NOT NULL,
    last_name              VARCHAR(100) NOT NULL,
    email                  VARCHAR(150) NOT NULL UNIQUE,
    date_of_birth          DATE         NOT NULL,
    photo_path             VARCHAR(255),
    security_question      VARCHAR(255) NOT NULL,
    security_answer_hash   VARCHAR(255) NOT NULL,
    security_answer_salt   VARCHAR(255) NOT NULL,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
