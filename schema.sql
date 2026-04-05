
CREATE DATABASE IF NOT EXISTS taskdb;
USE taskdb;

CREATE TABLE `users` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`password` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`role` VARCHAR(20) NOT NULL DEFAULT 'USER' COLLATE 'utf8mb4_0900_ai_ci',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `username` (`username`) USING BTREE,
	INDEX `idx_users_username` (`username`) USING BTREE
)


CREATE TABLE `tasks` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`description` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`status` ENUM('PENDING','IN_PROGRESS','COMPLETED') NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`created_by` BIGINT NOT NULL,
	`updated_by` BIGINT NOT NULL,
	`created_at` DATETIME NULL DEFAULT (CURRENT_TIMESTAMP),
	`updated_at` DATETIME NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `idx_tasks_created_by` (`created_by`) USING BTREE,
	CONSTRAINT `fk_task_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE
)


CREATE TABLE `refresh_tokens` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`token` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`expiry_date` DATETIME NOT NULL,
	`user_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `token` (`token`) USING BTREE,
	INDEX `fk_refresh_token_user` (`user_id`) USING BTREE,
	INDEX `idx_refresh_tokens_token` (`token`) USING BTREE,
	CONSTRAINT `fk_refresh_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE
)


