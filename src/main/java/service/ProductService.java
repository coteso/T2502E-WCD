package service;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Category;
import entity.Product;
import entity.ProductDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import repository.CategoryRepository;
import repository.ProductRepository;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class ProductService {

    public List<Product> searchProducts(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new ProductRepository(em).search(criteria);
        } finally {
            em.close();
        }
    }

    public long countProducts(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new ProductRepository(em).count(criteria);
        } finally {
            em.close();
        }
    }

    public Optional<Product> getProductByIdWithDetail(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new ProductRepository(em).findByIdWithDetail(id);
        } finally {
            em.close();
        }
    }

    // Tạo mới Product + ProductDetail cùng Transaction (Rollback nếu lỗi)
    public void createProduct(ProductFormDTO dto) throws Exception {
        validateDTO(dto, true);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ProductRepository productRepo = new ProductRepository(em);
            CategoryRepository categoryRepo = new CategoryRepository(em);

            // Kiểm tra trùng lặp SKU
            if (productRepo.existsBySku(dto.getSku().trim())) {
                throw new Exception("Mã SKU '" + dto.getSku() + "' đã tồn tại trong hệ thống!");
            }

            // Kiểm tra Category tồn tại và đang active
            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new Exception("Danh mục không tồn tại!"));
            if (!category.getStatus()) {
                throw new Exception("Không thể gán sản phẩm vào danh mục đang bị vô hiệu hóa!");
            }

            // Map Product
            Product product = new Product();
            product.setSku(dto.getSku().trim());
            product.setName(dto.getName().trim());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
            product.setDeleted(false);
            product.setCategory(category);

            // Map ProductDetail
            ProductDetail detail = new ProductDetail();
            detail.setManufacturer(dto.getManufacturer());
            detail.setWarrantyMonths(dto.getWarrantyMonths());
            detail.setOrigin(dto.getOrigin());
            detail.setDescription(dto.getDescription());
            detail.setTechnicalSpec(dto.getTechnicalSpec());

            // Thiết lập quan hệ 2 chiều (cascade ALL sẽ tự persist detail)
            product.setDetail(detail);

            productRepo.save(product);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Cập nhật Product + ProductDetail cùng Transaction
    public void updateProduct(ProductFormDTO dto) throws Exception {
        if (dto.getId() == null) {
            throw new Exception("ID sản phẩm không hợp lệ!");
        }
        validateDTO(dto, false);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ProductRepository productRepo = new ProductRepository(em);
            CategoryRepository categoryRepo = new CategoryRepository(em);

            Product product = productRepo.findByIdWithDetail(dto.getId())
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm có ID: " + dto.getId()));

            if (productRepo.existsBySkuAndIdNot(dto.getSku().trim(), dto.getId())) {
                throw new Exception("Mã SKU '" + dto.getSku() + "' đã được sử dụng bởi sản phẩm khác!");
            }

            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new Exception("Danh mục không tồn tại!"));
            if (!category.getStatus()) {
                throw new Exception("Không thể chọn danh mục đang bị vô hiệu hóa!");
            }

            product.setSku(dto.getSku().trim());
            product.setName(dto.getName().trim());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
            product.setCategory(category);

            ProductDetail detail = product.getDetail();
            if (detail == null) {
                detail = new ProductDetail();
                product.setDetail(detail);
            }
            detail.setManufacturer(dto.getManufacturer());
            detail.setWarrantyMonths(dto.getWarrantyMonths());
            detail.setOrigin(dto.getOrigin());
            detail.setDescription(dto.getDescription());
            detail.setTechnicalSpec(dto.getTechnicalSpec());

            productRepo.save(product);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Xoá mềm: set deleted = true, không xoá vật lý detail
    public void softDelete(Long id) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ProductRepository repo = new ProductRepository(em);
            Product product = repo.findByIdWithDetail(id)
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm cần xoá!"));

            product.setDeleted(true);
            repo.save(product);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Validate nghiệp vụ bắt buộc
    private void validateDTO(ProductFormDTO dto, boolean isCreate) throws Exception {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new Exception("Tên sản phẩm không được để trống!");
        }
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            throw new Exception("Mã SKU không được để trống!");
        }
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new Exception("Giá sản phẩm phải lớn hơn 0!");
        }
        if (dto.getQuantity() == null || dto.getQuantity() < 0) {
            throw new Exception("Số lượng tồn kho phải lớn hơn hoặc bằng 0!");
        }
        if (dto.getCategoryId() == null || dto.getCategoryId() <= 0) {
            throw new Exception("Vui lòng chọn danh mục cho sản phẩm!");
        }
        if (dto.getWarrantyMonths() != null && dto.getWarrantyMonths() < 0) {
            throw new Exception("Thời gian bảo hành phải lớn hơn hoặc bằng 0 tháng!");
        }
    }
}