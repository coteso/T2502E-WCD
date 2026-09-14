-- =====================================================================
-- Product ORM MVC - schema cho MySQL (tham khảo)
-- Hibernate chạy hbm2ddl.auto=update sẽ tự tạo bảng này, file này dùng
-- để nộp bài và cho phép tạo DB thủ công nếu muốn kiểm soát schema.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS product_orm_mvc_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE product_orm_mvc_db;

CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(150) NOT NULL,
    description TEXT,
    status      BIT          NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_name (name)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS products (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    sku         VARCHAR(64)   NOT NULL,
    name        VARCHAR(200)  NOT NULL,
    price       DECIMAL(12,2) NOT NULL,
    quantity    INT           NOT NULL,
    status      BIT           NOT NULL,
    deleted     BIT           NOT NULL,
    category_id BIGINT        NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_products_sku (sku),
    KEY idx_products_category (category_id),
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS product_details (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    product_id      BIGINT      NOT NULL,
    manufacturer    VARCHAR(150),
    warranty_months INT         NOT NULL,
    origin          VARCHAR(100),
    description     TEXT,
    technical_spec  TEXT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_details_product (product_id),
    CONSTRAINT fk_details_product
        FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE = InnoDB;
