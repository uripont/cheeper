USE ${DB_NAME};

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS student_degrees;
DROP TABLE IF EXISTS student_subjects;
DROP TABLE IF EXISTS student_social_links;
DROP TABLE IF EXISTS association;
DROP TABLE IF EXISTS entity;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS users;

-- Create the base users table
CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    biography VARCHAR(500),
    picture VARCHAR(255),
    role_type ENUM('STUDENT', 'GENERIC', 'ENTITY', 'ASSOCIATION') NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_username (username),
    INDEX idx_role_type (role_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the student table
CREATE TABLE student (
    student_id INT NOT NULL,
    birthdate DATE,
    PRIMARY KEY (student_id),
    CONSTRAINT fk_student_user 
        FOREIGN KEY (student_id) 
        REFERENCES users (id) 
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create normalized tables for student data
CREATE TABLE student_social_links (
    id INT NOT NULL AUTO_INCREMENT,
    student_id INT NOT NULL,
    platform VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_social_student 
        FOREIGN KEY (student_id) 
        REFERENCES student (student_id) 
        ON DELETE CASCADE,
    INDEX idx_platform (platform)
) ENGINE=InnoDB;

CREATE TABLE student_degrees (
    id INT NOT NULL AUTO_INCREMENT,
    student_id INT NOT NULL,
    degree_name VARCHAR(100) NOT NULL,
    institution VARCHAR(100) NOT NULL,
    year INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_degree_student 
        FOREIGN KEY (student_id) 
        REFERENCES student (student_id) 
        ON DELETE CASCADE,
    INDEX idx_year (year)
) ENGINE=InnoDB;

CREATE TABLE student_subjects (
    id INT NOT NULL AUTO_INCREMENT,
    student_id INT NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_subject_student 
        FOREIGN KEY (student_id) 
        REFERENCES student (student_id) 
        ON DELETE CASCADE,
    INDEX idx_semester (semester)
) ENGINE=InnoDB;

-- Create the entity table
CREATE TABLE entity (
    entity_id INT NOT NULL,
    department VARCHAR(100),
    PRIMARY KEY (entity_id),
    CONSTRAINT fk_entity_user 
        FOREIGN KEY (entity_id) 
        REFERENCES users (id) 
        ON DELETE CASCADE,
    INDEX idx_department (department)
) ENGINE=InnoDB;

-- Create the association table
CREATE TABLE association (
    association_id INT NOT NULL,
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    verification_date TIMESTAMP NULL,
    PRIMARY KEY (association_id),
    CONSTRAINT fk_association_user 
        FOREIGN KEY (association_id) 
        REFERENCES users (id) 
        ON DELETE CASCADE,
    INDEX idx_verification_status (verification_status)
) ENGINE=InnoDB;

-- Reset SQL mode and checks
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
