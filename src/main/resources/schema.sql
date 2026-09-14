-- Script khởi tạo Database cho Dự án Java Web MVC ORM
CREATE DATABASE IF NOT EXISTS `product_orm_mvc_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `product_orm_mvc_db`;

-- Table users
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(150),
    `role` VARCHAR(100) DEFAULT 'ADMIN',
    `status` BIT(1) NOT NULL DEFAULT b'1',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table categories
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL UNIQUE,
    `description` TEXT,
    `status` BIT(1) NOT NULL DEFAULT b'1',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table products
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sku` VARCHAR(100) NOT NULL UNIQUE,
    `name` VARCHAR(255) NOT NULL,
    `price` DECIMAL(15, 2) NOT NULL,
    `quantity` INT NOT NULL DEFAULT 0,
    `status` BIT(1) NOT NULL DEFAULT b'1',
    `deleted` BIT(1) NOT NULL DEFAULT b'0',
    `category_id` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table product_details
CREATE TABLE IF NOT EXISTS `product_details` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL UNIQUE,
    `manufacturer` VARCHAR(150),
    `warranty_months` INT DEFAULT 0,
    `origin` VARCHAR(100),
    `description` TEXT,
    `technical_spec` TEXT,
    CONSTRAINT `fk_product_details_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
