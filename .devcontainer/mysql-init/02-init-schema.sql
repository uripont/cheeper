-- 02-init-schema.sql
-- This file is loaded by 01-init-users.sh into the database named by $MYSQL_DATABASE

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS room_participants;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS followers;
DROP TABLE IF EXISTS association;
DROP TABLE IF EXISTS entity;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS users;

-- Create the base users table
CREATE TABLE users (
  id INT            NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  email     VARCHAR(100) NOT NULL,
  username  VARCHAR(20)  NOT NULL,
  biography VARCHAR(500),
  picture   VARCHAR(255),
  role_type ENUM('STUDENT','GENERIC','ENTITY','ASSOCIATION') NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (email),
  UNIQUE KEY (username)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- Create the student table
CREATE TABLE student (
  student_id        INT      NOT NULL,
  birthdate         DATE,
  social_links      JSON,
  degrees           JSON,
  enrolled_subjects JSON,
  PRIMARY KEY (student_id),
  CONSTRAINT fk_student_user FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create the entity table
CREATE TABLE entity (
  entity_id INT      NOT NULL,
  department VARCHAR(100),
  PRIMARY KEY (entity_id),
  CONSTRAINT fk_entity_user FOREIGN KEY (entity_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create the association table
CREATE TABLE association (
  association_id     INT      NOT NULL,
  verification_status VARCHAR(50),
  verification_date   TIMESTAMP NULL,
  PRIMARY KEY (association_id),
  CONSTRAINT fk_association_user FOREIGN KEY (association_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create followers table
CREATE TABLE followers (
  follower_id   INT NOT NULL,
  following_id  INT NOT NULL,
  status        ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (follower_id, following_id),
  CONSTRAINT fk_follower_user   FOREIGN KEY (follower_id)  REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_following_user  FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
  CHECK (follower_id <> following_id)
) ENGINE=InnoDB;

-- Create post table
CREATE TABLE post (
  post_id     INT NOT NULL AUTO_INCREMENT,
  source_id   INT,            -- For reposts/quotes
  user_id     INT NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  image       VARCHAR(255),
  content     TEXT,
  PRIMARY KEY (post_id),
  CONSTRAINT fk_post_user   FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_post_source FOREIGN KEY (source_id) REFERENCES post(post_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Create likes table
CREATE TABLE likes (
  post_id    INT NOT NULL,
  user_id    INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (post_id, user_id),
  CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id)    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create room table for temporary chats
CREATE TABLE room (
  room_id    INT NOT NULL AUTO_INCREMENT,
  name       VARCHAR(100),
  description VARCHAR(255),
  created_by INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NULL,       -- NULL means permanent room
  is_active  BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (room_id),
  CONSTRAINT fk_room_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create room participants table
CREATE TABLE room_participants (
  room_id    INT NOT NULL,
  user_id    INT NOT NULL,
  joined_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_admin   BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (room_id, user_id),
  CONSTRAINT fk_participant_room FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE CASCADE,
  CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users(id)     ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create messages table
CREATE TABLE messages (
  message_id INT NOT NULL AUTO_INCREMENT,
  room_id    INT NOT NULL,
  sender_id  INT NOT NULL,
  content    TEXT    NOT NULL,
  sent_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (message_id),
  CONSTRAINT fk_message_room   FOREIGN KEY (room_id)   REFERENCES room(room_id)   ON DELETE CASCADE,
  CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id)       ON DELETE CASCADE
) ENGINE=InnoDB;

-- Index for more efficient room message lookup
CREATE INDEX idx_room_messages ON messages(room_id, sent_at);

-- Triggers for bidirectional cascading deletes back to users
DELIMITER $$
CREATE TRIGGER after_student_delete
  AFTER DELETE ON student
  FOR EACH ROW
BEGIN
  DELETE FROM users WHERE id = OLD.student_id;
END$$

CREATE TRIGGER after_entity_delete
  AFTER DELETE ON entity
  FOR EACH ROW
BEGIN
  DELETE FROM users WHERE id = OLD.entity_id;
END$$

CREATE TRIGGER after_association_delete
  AFTER DELETE ON association
  FOR EACH ROW
BEGIN
  DELETE FROM users WHERE id = OLD.association_id;
END$$
DELIMITER ;