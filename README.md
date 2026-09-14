# Đồ Án Java Web MVC - ORM (JPA / Hibernate / MySQL)

Dự án quản lý sản phẩm xây dựng chuẩn theo mô hình MVC 3 lớp, sử dụng JPA/Hibernate ORM kết nối cơ sở dữ liệu MySQL trên Tomcat Server.

---

## Công Nghệ & Thư Viện Sử Dụng

- **Ngôn ngữ & Nền tảng**: Java 17/21/25 (Jakarta EE 10 / Servlet 6.0)
- **MVC Framework**: Jakarta Servlet + JSP + JSTL (Jakarta Namespace)
- **ORM Framework**: JPA / Hibernate Core 6.6
- **Database**: MySQL 8.x (XAMPP / Standalone)
- **Build Tool**: Maven

---

## Cấu Trúc Dự Án (Project Structure)

```text
src/main/java
 ├── controller
 │   ├── ProductServlet.java       # Điều hướng /products, /products/create, /products/edit, /products/delete
 │   └── CategoryServlet.java      # Điều hướng /categories
 ├── service
 │   ├── ProductService.java       # Quản lý Transaction & Validate nghiệp vụ sản phẩm
 │   └── CategoryService.java      # Quản lý nghiệp vụ danh mục và ràng buộc active
 ├── repository
 │   ├── ProductRepository.java    # JPQL JOIN FETCH, search, count, soft delete
 │   └── CategoryRepository.java   # JPQL CRUD Category
 ├── entity
 │   ├── Category.java             # Entity @OneToMany với Product
 │   ├── Product.java              # Entity @ManyToOne với Category, @OneToOne với ProductDetail
 │   └── ProductDetail.java        # Entity @OneToOne với Product
 ├── dto
 │   ├── ProductFormDTO.java       # DTO hợp nhất dữ liệu Form
 │   └── ProductSearchDTO.java     # DTO tham số bộ lọc & phân trang
 └── util
     └── JPAUtil.java              # Singleton EntityManagerFactory (product_orm_mvc_pu)
src/main/resources
 ├── META-INF/persistence.xml      # Cấu hình Hibernate Dialect & MySQL Connection
 ├── schema.sql                    # Script khởi tạo cơ sở dữ liệu
 └── data.sql                      # Script dữ liệu mẫu
src/main/webapp
 ├── index.jsp                     # Chuyển hướng tự động về /products
 └── views
     ├── product-list.jsp          # Giao diện danh sách, tìm kiếm, lọc, phân trang
     ├── product-form.jsp          # Form tạo/sửa Product + Detail trên 1 màn hình
     ├── category-list.jsp         # Giao diện quản lý danh mục
     └── error.jsp                 # Màn hình thông báo lỗi
```

---

## Hướng Dẫn Cấu Hình Cơ Sở Dữ Liệu

1. Khởi động **MySQL** trên **XAMPP Control Panel** (Port mặc định: `3306`).
2. Mở phpMyAdmin (`http://localhost/phpmyadmin`) hoặc phần mềm quản lý MySQL (DBeaver, MySQL Workbench) và chạy lệnh:
   ```sql
   CREATE DATABASE product_orm_mvc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Chạy script dữ liệu mẫu trong file `src/main/resources/data.sql` (hoặc `schema.sql`) để khởi tạo các bảng và dữ liệu thử nghiệm.
4. Hibernate cũng được cấu hình `hibernate.hbm2ddl.auto = update` trong `persistence.xml` để tự động tạo/cập nhật bảng khi ứng dụng khởi chạy.

---

## Hướng Dẫn Biên Dịch & Chạy Ứng Dụng

### Biên dịch dự án qua Maven:
```bash
mvn clean package
```
File WAR xuất ra tại: `target/mvc-product-demo.war`.

### Triển khai trên Tomcat Server:
1. Copy file `mvc-product-demo.war` vào thư mục `webapps/` của Apache Tomcat (hỗ trợ Jakarta EE 10 / Tomcat 10.1.x).
2. Khởi động Tomcat.
3. Truy cập địa chỉ: `http://localhost:8080/mvc-product-demo/products`

---

## Tính Năng Nổi Bật

1. **Transaction Hợp Nhất**: Tạo hoặc Cập nhật `Product` và `ProductDetail` đồng thời trong một Transaction duy nhất. Tự động `rollback()` nếu có bất kỳ lỗi nào xảy ra.
2. **Ngăn Chặn N+1 Query**: Tìm kiếm sản phẩm bằng JPQL `JOIN FETCH` đồng thời `Category` và `ProductDetail`.
3. **Soft Delete (Xóa Mềm)**: Xóa sản phẩm đánh dấu `deleted = true`, giữ nguyên thông tin `ProductDetail` trong DB.
4. **Kiểm Soát Ràng Buộc Danh Mục**: Không cho phép vô hiệu hóa (Disable) Danh mục nếu vẫn còn Sản phẩm Active thuộc danh mục đó.
5. **Giao Diện Hiện Đại & Clean Code**: Sử dụng 100% JSTL/EL trong JSP, tuyệt đối không dùng scriptlet.
