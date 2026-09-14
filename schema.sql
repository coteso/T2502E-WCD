-- 1. Tạo Database và sử dụng Database
CREATE DATABASE IF NOT EXISTS product_orm_mvc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE product_orm_mvc_db;

-- Xóa bảng cũ nếu đã tồn tại để tránh xung đột khóa ngoại
DROP TABLE IF EXISTS product_details;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;

-- 2. Bảng categories (10 bản ghi)
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            description VARCHAR(255),
                            status BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at DATETIME,
                            updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO categories (id, name, description, status, created_at, updated_at) VALUES
                                                                                   (1, 'Điện Thoại', 'Các dòng điện thoại thông minh chính hãng', TRUE, NOW(), NOW()),
                                                                                   (2, 'Laptop', 'Laptop văn phòng, gaming và máy trạm', TRUE, NOW(), NOW()),
                                                                                   (3, 'Máy Tính Bảng', 'Các loại máy tính bảng Android và iPad', TRUE, NOW(), NOW()),
                                                                                   (4, 'Phụ Kiện Điện Tử', 'Tai nghe, cáp sạc, sạc dự phòng', TRUE, NOW(), NOW()),
                                                                                   (5, 'Đồng Hồ Thông Minh', 'Smartwatch theo dõi sức khỏe và thể thao', TRUE, NOW(), NOW()),
                                                                                   (6, 'Linh Kiện PC', 'RAM, SSD, Card màn hình, CPU', TRUE, NOW(), NOW()),
                                                                                   (7, 'Màn Hình', 'Màn hình máy tính đồ họa và gaming', TRUE, NOW(), NOW()),
                                                                                   (8, 'Thiết Bị Âm Thanh', 'Loa Bluetooth, tai nghe chụp tai', TRUE, NOW(), NOW()),
                                                                                   (9, 'Thiết Bị Mạng', 'Router Wi-Fi, bộ phát sóng, switch', FALSE, NOW(), NOW()),
                                                                                   (10, 'Thiết Bị Văn Phòng', 'Máy in, máy chiếu, bàn phím, chuột', TRUE, NOW(), NOW());

-- 3. Bảng products (10 bản ghi)
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          sku VARCHAR(255) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          price DECIMAL(15, 2) NOT NULL,
                          quantity INT NOT NULL,
                          status BOOLEAN NOT NULL DEFAULT TRUE,
                          deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          category_id BIGINT NOT NULL,
                          created_at DATETIME,
                          updated_at DATETIME,
                          CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO products (id, sku, name, price, quantity, status, deleted, category_id, created_at, updated_at) VALUES
                                                                                                                (1, 'SKU-IP15PM', 'iPhone 15 Pro Max 256GB', 29990000.00, 15, TRUE, FALSE, 1, NOW(), NOW()),
                                                                                                                (2, 'SKU-SS-S24U', 'Samsung Galaxy S24 Ultra', 26990000.00, 20, TRUE, FALSE, 1, NOW(), NOW()),
                                                                                                                (3, 'SKU-MBP-M3', 'MacBook Pro 14 M3 2023', 39990000.00, 8, TRUE, FALSE, 2, NOW(), NOW()),
                                                                                                                (4, 'SKU-ROG-G16', 'ASUS ROG Strix G16 Gaming', 32500000.00, 12, TRUE, FALSE, 2, NOW(), NOW()),
                                                                                                                (5, 'SKU-IPAD-M2', 'iPad Air 5 M2 Wi-Fi', 14990000.00, 25, TRUE, FALSE, 3, NOW(), NOW()),
                                                                                                                (6, 'SKU-AP-PRO2', 'AirPods Pro Gen 2 Type-C', 5690000.00, 50, TRUE, FALSE, 4, NOW(), NOW()),
                                                                                                                (7, 'SKU-AW-S9', 'Apple Watch Series 9 41mm', 8990000.00, 18, TRUE, FALSE, 5, NOW(), NOW()),
                                                                                                                (8, 'SKU-SSD-990', 'SSD Samsung 990 Pro 1TB', 2850000.00, 40, TRUE, FALSE, 6, NOW(), NOW()),
                                                                                                                (9, 'SKU-LG-27GP', 'Màn Hình LG UltraGear 27 inch', 6490000.00, 10, TRUE, FALSE, 7, NOW(), NOW()),
                                                                                                                (10, 'SKU-MX-M3S', 'Chuột Logitech MX Master 3S', 2290000.00, 30, TRUE, FALSE, 10, NOW(), NOW());

-- 4. Bảng product_details (10 bản ghi liên kết 1-1 tương ứng với 10 Product trên)
CREATE TABLE product_details (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 product_id BIGINT NOT NULL UNIQUE,
                                 manufacturer VARCHAR(255),
                                 warranty_months INT,
                                 origin VARCHAR(255),
                                 description TEXT,
                                 technical_spec TEXT,
                                 CONSTRAINT fk_detail_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO product_details (id, product_id, manufacturer, warranty_months, origin, description, technical_spec) VALUES
                                                                                                                     (1, 1, 'Apple', 12, 'Trung Quốc', 'Khung vỏ Titanium siêu bền, chip A17 Pro mạnh mẽ', 'Màn hình 6.7" OLED 120Hz, RAM 8GB, Pin 4422 mAh'),
                                                                                                                     (2, 2, 'Samsung', 12, 'Việt Nam', 'Tích hợp bút S-Pen và tính năng Galaxy AI thông minh', 'Màn hình 6.8" Dynamic AMOLED 2X, Chip Snapdragon 8 Gen 3'),
                                                                                                                     (3, 3, 'Apple', 12, 'Trung Quốc', 'Laptop cao cấp dành cho lập trình viên và nhà thiết kế', 'Màn hình Liquid Retina XDR, Chip M3 8-core CPU, RAM 16GB, SSD 512GB'),
                                                                                                                     (4, 4, 'ASUS', 24, 'Đài Loan', 'Laptop gaming cấu hình khủng trang bị hệ thống tản nhiệt tiên tiến', 'Intel Core i7-13650HX, RTX 4060 8GB, RAM 16GB DDR5, SSD 1TB'),
                                                                                                                     (5, 5, 'Apple', 12, 'Trung Quốc', 'Máy tính bảng siêu nhẹ hiệu năng cao với chip M2', 'Màn hình 10.9" Liquid Retina, Chip Apple M2, RAM 8GB'),
                                                                                                                     (6, 6, 'Apple', 12, 'Việt Nam', 'Tai nghe chống ồn chủ động cao cấp cổng sạc USB-C', 'Chip H2, Chống ồn ANC, Pin lên đến 30h kèm hộp sạc'),
                                                                                                                     (7, 7, 'Apple', 12, 'Trung Quốc', 'Đồng hồ thông minh hỗ trợ tính năng chạm hai lần (Double Tap)', 'Màn hình Always-On Retina 2000 nits, Đo nhịp tim, SpO2, Chống nước 50m'),
                                                                                                                     (8, 8, 'Samsung', 60, 'Hàn Quốc', 'SSD M.2 NVMe PCIe 4.0 tốc độ đọc ghi cực nhanh', 'Tốc độ đọc 7450 MB/s, Tốc độ ghi 6900 MB/s, Tuổi thọ 600 TBW'),
                                                                                                                     (9, 9, 'LG', 24, 'Việt Nam', 'Màn hình gaming tấm nền Nano IPS màu sắc chuẩn xác', 'Độ phân giải QHD (2560x1440), Tần số quét 144Hz, 1ms GtG'),
                                                                                                                     (10, 10, 'Logitech', 12, 'Trung Quốc', 'Chuột không dây công xưởng siêu êm dành cho lập trình viên', 'Cảm biến Darkfield 8000 DPI, Cuộn vô cực MagSpeed, Kết nối 3 thiết bị');