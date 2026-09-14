# Product ORM MVC — Java Web (JSP/Servlet + JPA/Hibernate)

Ứng dụng quản lý sản phẩm theo **kiến trúc MVC nhiều lớp** thực hiện đề bài thực hành
"Java Web MVC - ORM/JPA/Hibernate": `Controller → Service → Repository → Entity (JPA) → View (JSP/JSTL/EL)`,
không dùng JDBC thuần, không viết SQL/EntityManager trong JSP.

## 1. Công nghệ

| Thành phần | Phiên bản |
|---|---|
| Java | 17 |
| Tomcat | 10.1.x (Servlet 6.0, namespace `jakarta.*`) |
| Hibernate ORM | 6.4.x |
| Jakarta Persistence | 3.1 |
| MySQL | 8.x (driver `mysql-connector-j` 8.3) |
| JSTL | jakarta.servlet.jsp.jstl 3.0 |
| Build | Maven (`mvnw`) — WAR: `javaWebMVC.war` |

## 2. Cấu trúc project

```
src/main/java/com/example/javawebmvc
├── controller/   ProductServlet, CategoryServlet
├── service/      ProductService, CategoryService  (nghiệp vụ + transaction)
├── repository/   ProductRepository, CategoryRepository (JPQL/Criteria)
├── entity/       Category, Product, ProductDetail
├── dto/          ProductFormDTO, ProductSearchDTO, PageResult, CategoryRow
├── exception/    BusinessException, ValidationException
└── util/         JPAUtil, JpaLifecycleListener

src/main/resources
├── META-INF/persistence.xml   (persistence-unit: productORMPU)
├── schema.sql, data.sql       (tạo DB + dữ liệu mẫu, nộp bài)
```

Views: `src/main/webapp/WEB-INF/views/{product-list,product-form,category-list,error}.jsp`.

## 3. Cấu hình database

1. Cài MySQL 8, đảm bảo chạy ở port **3307** (hoặc sửa lại URL bên dưới).
2. Sửa `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:mysql://localhost:3307/product_orm_mvc_db?useSSL=false&amp;serverTimezone=Asia/Ho_Chi_Minh&amp;allowPublicKeyRetrieval=true"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="123456"/>
```

3. Tạo database (tuỳ chọn — Hibernate `hbm2ddl.auto=update` tự tạo bảng khi chạy):

```bash
mysql -u root -p < src/main/resources/schema.sql
mysql -u root -p < src/main/resources/data.sql
```

## 4. Chạy project

```bash
# Đóng gói WAR
./mvnw clean package

# Copy target/javaWebMVC.war vào webapps/ của Tomcat 10.1 và khởi động Tomcat
```

Hoặc chạy trực tiếp trong IntelliJ IDEA: Run → Edit Configurations → Tomcat Server 10.1.x →
Deployment → thêm artifact `javaWebMVC:war exploded` → Application context `/javaWebMVC` (hoặc `/`).

Truy cập: `http://localhost:8080/javaWebMVC/` (trang chủ) → `/products` và `/categories`.

## 5. Tài khoản / database mẫu

Không có đăng nhập. Database mẫu trong `data.sql`: 3 danh mục (Điện thoại, Laptop, Phụ kiện),
5 sản phẩm đang bán kèm ProductDetail, 1 sản phẩm đã xoá mềm (`deleted = 1`).

## 6. Tính năng chính

- **Category**: thêm/sửa, vô hiệu hoá (chặn khi còn product active), xoá (chặn khi còn product tham chiếu). Bảng hiển thị kèm số SP active/tổng — một query duy nhất, chống N+1.
- **Product**: tạo/sửa Product + ProductDetail **trong một transaction** (cascade ALL, orphanRemoval; Detail lỗi ⇒ rollback cả Product). SKU tự sinh `SKU-yyyyMMdd-HHmmss-xxx` nếu để trống; kiểm tra unique.
- **Danh sách nâng cao**: keyword (name/sku), lọc category/status/khoảng giá, sort name|price|createdAt asc|desc, phân trang — bằng **Criteria API** động, sort theo whitelist.
- **Soft delete**: `POST /products/delete` chỉ set `deleted = true`; Detail và Category giữ nguyên.
- **Validate**: tên rỗng, giá ≤ 0, tồn kho < 0, bảo hành < 0, thiếu danh mục, SKU trùng, danh mục inactive ⇒ hiển thị lỗi ngay trên form, dữ liệu nhập được giữ lại.
- **Error handling**: `error.jsp` + `error-page` 404/500/Exception; thông báo rõ khi id không tồn tại, SKU trùng, category invalid.

## 7. Ánh xạ tới tiêu chí chấm điểm

| Tiêu chí | Nơi cài đặt |
|---|---|
| MVC + Service + Repository | package `controller/service/repository` |
| Mapping 3 entity 1-n, 1-1 | `entity/` + `repository` join fetch |
| CRUD Product+Detail 1 transaction | `ProductService.create/update` + `JPAUtil.runInTransaction*` |
| Ràng buộc category | `CategoryService.setStatus/delete` |
| Search/sort/pagination | `ProductRepository.search/count` (Criteria) |
| Validate + rollback | `ProductService.validate` + `ValidationException` |
| Soft delete, id không tồn tại | `ProductService.softDelete`, `error.jsp` |
| DTO + JPAUtil | `dto/`, `util/JPAUtil` |
| JSP JSTL/EL | `views/` (0 scriptlet) |

## 8. Lưu ý nộp bài

- Chụp màn hình: trang category, list + filter, create, edit, soft delete.
- Nếu đổi port/credentials MySQL, chỉ cần sửa `persistence.xml`.
- `hibernate.show_sql=true` đang bật để demo; có thể tắt khi nộp.
