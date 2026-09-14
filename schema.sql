-- ===== TẠO DATABASE & TABLES =====
-- Chạy script này trong MySQL để tạo database và bảng cho project ORM

-- Tạo database
CREATE DATABASE IF NOT EXISTS product_orm_mvc_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Sử dụng database
USE product_orm_mvc_db;

-- ===== TABLE CATEGORIES =====
-- Bảng danh mục sản phẩm
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== TABLE PRODUCTS =====
-- Bảng sản phẩm
-- FOREIGN KEY: category_id tham chiếu đến categories.id
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    description TEXT,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Tạo khóa ngoại (foreign key)
    CONSTRAINT fk_product_category 
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== TABLE PRODUCT_DETAILS =====
-- Bảng chi tiết sản phẩm (quan hệ 1-1 với products)
CREATE TABLE product_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL UNIQUE,  -- UNIQUE: mỗi product chỉ có 1 detail
    manufacturer VARCHAR(100) NOT NULL,
    warranty_months INT NOT NULL,
    origin VARCHAR(100) NOT NULL,
    description TEXT,
    technical_spec TEXT,
    
    -- Tạo khóa ngoại
    CONSTRAINT fk_productdetail_product 
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== INDEXES =====
-- Tạo index để tối ưu performance cho các query thường dùng
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_deleted ON products(deleted);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_name ON products(name);

-- ===== DỮ LIỆU MẪU =====
-- Chèn dữ liệu mẫu để test

-- Thêm danh mục mẫu
INSERT INTO categories (name, description, status) VALUES
('Điện tử', 'Các sản phẩm điện tử', TRUE),
('Gia dụng', 'Các sản phẩm gia dụng', TRUE),
('Quần áo', 'Các sản phẩm thời trang', TRUE);

-- Thêm sản phẩm mẫu
INSERT INTO products (sku, name, price, quantity, description, status, category_id) VALUES
('SKU001', 'Laptop Dell XPS 13', 1200.00, 10, 'Laptop hiệu năng cao', TRUE, 1),
('SKU002', 'Chuột Logitech Wireless', 25.50, 50, 'Chuột không dây tiện lợi', TRUE, 1),
('SKU003', 'Nước rửa chén F10', 5.00, 100, 'Nước rửa chén hiệu quả', TRUE, 2),
('SKU004', 'Áo phông Nam Uniqlo', 12.00, 200, 'Áo phông 100% cotton', TRUE, 3);

-- Thêm chi tiết sản phẩm mẫu
INSERT INTO product_details (product_id, manufacturer, warranty_months, origin, description) VALUES
(1, 'Dell Technologies', 12, 'USA', 'Laptop Intel Core i7, 16GB RAM, 512GB SSD'),
(2, 'Logitech International', 24, 'China', 'Chuột USB 2.4GHz, 3000 DPI'),
(3, 'Liên Hợp Gida Ltd.', 36, 'Việt Nam', 'Nước rửa chén hương chanh mạnh'),
(4, 'Fast Retailing Co', 6, 'Bangladesh', 'Áo phông vừa vặn, thoáng mát');

-- Kiểm tra dữ liệu đã thêm
SELECT 'Categories:' AS '';
SELECT * FROM categories;

SELECT 'Products:' AS '';
SELECT p.id, p.sku, p.name, p.price, c.name AS category, p.status, p.deleted 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id;

SELECT 'Product Details:' AS '';
SELECT * FROM product_details;
