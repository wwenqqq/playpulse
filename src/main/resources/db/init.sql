-- GameShelf reference schema for local inspection.
-- Docker and Kubernetes use Hibernate schema management on the feedback service;
-- this file is intentionally not mounted into the MySQL container.

CREATE DATABASE IF NOT EXISTS playpulse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE playpulse;

CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT NOT NULL AUTO_INCREMENT, username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL, created_at DATETIME(6), updated_at DATETIME(6),
  PRIMARY KEY (id), UNIQUE KEY uk_app_user_username (username)
);

CREATE TABLE IF NOT EXISTS game (
  id BIGINT NOT NULL AUTO_INCREMENT, title VARCHAR(120) NOT NULL,
  studio VARCHAR(90) NOT NULL, release_year INT NOT NULL,
  genres VARCHAR(120) NOT NULL, cover_tone VARCHAR(24) NOT NULL,
  summary TEXT NOT NULL, created_by BIGINT NULL, created_at DATETIME(6),
  PRIMARY KEY (id), UNIQUE KEY uk_game_title (title), KEY idx_game_created_by (created_by)
);

CREATE TABLE IF NOT EXISTS library_entry (
  id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, game_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL, created_at DATETIME(6), PRIMARY KEY (id),
  UNIQUE KEY uk_library_user_game (user_id, game_id), KEY idx_library_game (game_id)
);

CREATE TABLE IF NOT EXISTS feedback (
  id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, game_id BIGINT NULL,
  title VARCHAR(160) NOT NULL, description TEXT NOT NULL, category VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL, priority VARCHAR(20) NOT NULL, game_version VARCHAR(30),
  created_at DATETIME(6), updated_at DATETIME(6), PRIMARY KEY (id),
  KEY idx_feedback_game (game_id), KEY idx_feedback_user (user_id)
);

CREATE TABLE IF NOT EXISTS feedback_vote (
  id BIGINT NOT NULL AUTO_INCREMENT, feedback_id BIGINT NOT NULL, user_id BIGINT NOT NULL,
  PRIMARY KEY (id), UNIQUE KEY uk_feedback_vote (feedback_id, user_id)
);

CREATE TABLE IF NOT EXISTS feedback_comment (
  id BIGINT NOT NULL AUTO_INCREMENT, feedback_id BIGINT NOT NULL, user_id BIGINT NOT NULL,
  content TEXT NOT NULL, created_at DATETIME(6), PRIMARY KEY (id), KEY idx_feedback_comment (feedback_id)
);

CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NULL, username VARCHAR(50),
  operation VARCHAR(100), method VARCHAR(200), params TEXT, result TEXT, ip VARCHAR(50),
  execute_time BIGINT, created_at DATETIME(6), PRIMARY KEY (id),
  KEY idx_operation_user (user_id), KEY idx_operation_created (created_at)
);
