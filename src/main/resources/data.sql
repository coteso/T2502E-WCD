-- Script chèn dữ liệu mẫu cho Database product_orm_mvc_db
USE `product_orm_mvc_db`;

-- Sample Categories
INSERT INTO `categories` (`id`, `name`, `description`, `status`) VALUES
(1, 'Điện Thoại & Máy Tính Bảng', 'Các dòng điện thoại thông minh và máy tính bảng mới nhất', 1),
(2, 'Laptop & PC', 'Máy tính xách tay và máy tính để bàn cấu hình cao', 1),
(3, 'Phụ Kiện Công Nghệ', 'Tai nghe, bàn phím, chuột và sạc dự phòng', 1),
(4, 'Thiết Bị Cũ', 'Các dòng máy đã ngưng kinh doanh', 0)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- Sample Products
INSERT INTO `products` (`id`, `sku`, `name`, `price`, `quantity`, `status`, `deleted`, `category_id`) VALUES
(1, 'SKU-IP15PM-256', 'iPhone 15 Pro Max 256GB', 32990000.00, 15, 1, 0, 1),
(2, 'SKU-SS-S24U-512', 'Samsung Galaxy S24 Ultra 512GB', 33990000.00, 10, 1, 0, 1),
(3, 'SKU-MACBOOK-M3', 'MacBook Pro 14 inch M3 Pro', 49990000.00, 8, 1, 0, 2),
(4, 'SKU-ASUS-ROG-G16', 'Laptop Gaming ASUS ROG Strix G16', 38500000.00, 5, 1, 0, 2),
(5, 'SKU-SONY-WH1000XM5', 'Tai nghe Chống Ồn Sony WH-1000XM5', 8490000.00, 20, 1, 0, 3)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- Sample Product Details
INSERT INTO `product_details` (`id`, `product_id`, `manufacturer`, `warranty_months`, `origin`, `description`, `technical_spec`) VALUES
(1, 1, 'Apple Inc.', 12, 'Mỹ / Trung Quốc', 'Phiên bản Titan Tự Nhiên cao cấp với chip A17 Pro siêu mạnh', 'Màn hình 6.7 inch Super Retina XDR 120Hz, Chip A17 Pro, RAM 8GB, Camera 48MP'),
(2, 2, 'Samsung Electronics', 12, 'Hàn Quốc / Việt Nam', 'Flagship AI Phone đỉnh cao năm 2024 với bút S-Pen tích hợp', 'Màn hình 6.8 inch Dynamic AMOLED 2X, Snapdragon 8 Gen 3 for Galaxy, RAM 12GB'),
(3, 3, 'Apple Inc.', 24, 'Mỹ', 'Dòng máy tính xách tay chuyên nghiệp dành cho lập trình viên và creator', 'Chip M3 Pro (11-core CPU, 14-core GPU), RAM 18GB Unified Memory, SSD 512GB'),
(4, 4, 'ASUS', 24, 'Đài Loan', 'Laptop chơi game cấu hình khủng trang bị card đồ họa RTX 4070', 'CPU Intel Core i9-13980HX, GPU RTX 4070 8GB, RAM 16GB DDR5, Màn 240Hz'),
(5, 5, 'Sony Corporation', 12, 'Nhật Bản / Malaysia', 'Tai nghe trùm đầu chống ồn hàng đầu thế giới với thời lượng pin 30h', 'Driver 30mm, Bluetooth 5.2, Hỗ trợ LDAC, Chống ồn chủ động HD QN1')
ON DUPLICATE KEY UPDATE `manufacturer` = VALUES(`manufacturer`);
