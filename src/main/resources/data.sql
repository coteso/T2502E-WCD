-- =====================================================================
-- Dữ liệu mẫu - chạy sau schema.sql (hoặc để Hibernate tự tạo bảng rồi chạy)
-- =====================================================================
USE product_orm_mvc_db;

INSERT INTO categories (name, description, status, created_at, updated_at) VALUES
('Điện thoại', 'Điện thoại thông minh, phụ kiện', 1, NOW(), NOW()),
('Laptop', 'Máy tính xách tay văn phòng và gaming', 1, NOW(), NOW()),
('Phụ kiện', 'Sạc, tai nghe, bao da...', 1, NOW(), NOW());

INSERT INTO products (sku, name, price, quantity, status, deleted, category_id, created_at, updated_at) VALUES
('SKU-20260901-090000-001', 'iPhone 17 Pro Max', 34990000.00, 25, 1, 0, 1, NOW(), NOW()),
('SKU-20260901-090100-002', 'Samsung Galaxy S27 Ultra', 29490000.00, 18, 1, 0, 1, NOW(), NOW()),
('SKU-20260901-090200-003', 'MacBook Air M5 13"', 27990000.00, 12, 1, 0, 2, NOW(), NOW()),
('SKU-20260901-090300-004', 'Asus ROG Strix G18', 45990000.00, 6, 0, 0, 2, NOW(), NOW()),
('SKU-20260901-090400-005', 'Sạc nhanh 65W GaN', 590000.00, 120, 1, 0, 3, NOW(), NOW());

INSERT INTO product_details (product_id, manufacturer, warranty_months, origin, description, technical_spec) VALUES
(1, 'Apple', 12, 'Trung Quốc', 'Điện thoại flagship của Apple', 'A19 Pro, 8GB RAM, 256GB'),
(2, 'Samsung', 12, 'Hàn Quốc', 'Flagship Android của Samsung', 'Snapdragon 8 Elite 5, 12GB RAM, 256GB'),
(3, 'Apple', 12, 'Việt Nam', 'Laptop mỏng nhẹ cho văn phòng', 'M5, 16GB RAM, 512GB SSD'),
(4, 'Asus', 24, 'Trung Quốc', 'Laptop gaming hiệu năng cao', 'RTX 5070, i9-14900HX, 32GB RAM'),
(5, 'Anker', 6, 'Trung Quốc', 'Sạc nhanh đa cổng', '2x USB-C PD, 1x USB-A, 65W');

-- Sản phẩm đã bị xoá mềm (deleted = 1) - để kiểm chứng chức năng soft delete
INSERT INTO products (sku, name, price, quantity, status, deleted, category_id, created_at, updated_at) VALUES
('SKU-20260801-120000-099', 'Nokia 1100 (kỷ niệm)', 350000.00, 1, 0, 1, 3, NOW(), NOW());
