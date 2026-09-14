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

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private final ProductRepository productRepository = new ProductRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Product> searchProducts(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return productRepository.search(criteria, em);
        } finally {
            em.close();
        }
    }

    public long countProducts(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return productRepository.count(criteria, em);
        } finally {
            em.close();
        }
    }

    public Product getProductById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return productRepository.findByIdWithDetail(id, em)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id));
        } finally {
            em.close();
        }
    }

    public ProductFormDTO getProductFormDTOById(Long id) {
        Product product = getProductById(id);
        ProductFormDTO dto = new ProductFormDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setStatus(product.getStatus());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        ProductDetail detail = product.getDetail();
        if (detail != null) {
            dto.setManufacturer(detail.getManufacturer());
            dto.setWarrantyMonths(detail.getWarrantyMonths());
            dto.setOrigin(detail.getOrigin());
            dto.setDetailDescription(detail.getDescription());
            dto.setTechnicalSpec(detail.getTechnicalSpec());
        }

        return dto;
    }

    public void createProduct(ProductFormDTO dto) {
        validateFormDTO(dto, null);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (productRepository.existsBySku(dto.getSku(), em)) {
                throw new IllegalArgumentException("Mã SKU '" + dto.getSku().trim() + "' đã tồn tại.");
            }

            Category category = categoryRepository.findById(dto.getCategoryId(), em)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục lựa chọn không tồn tại."));

            if (Boolean.FALSE.equals(category.getStatus())) {
                throw new IllegalArgumentException("Không thể gán sản phẩm cho danh mục đang bị vô hiệu hóa.");
            }

            Product product = new Product();
            product.setSku(dto.getSku().trim().toUpperCase());
            product.setName(dto.getName().trim());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
            product.setCategory(category);

            ProductDetail detail = new ProductDetail();
            detail.setManufacturer(dto.getManufacturer() != null ? dto.getManufacturer().trim() : null);
            detail.setWarrantyMonths(dto.getWarrantyMonths() != null ? dto.getWarrantyMonths() : 0);
            detail.setOrigin(dto.getOrigin() != null ? dto.getOrigin().trim() : null);
            detail.setDescription(dto.getDetailDescription() != null ? dto.getDetailDescription().trim() : null);
            detail.setTechnicalSpec(dto.getTechnicalSpec() != null ? dto.getTechnicalSpec().trim() : null);

            product.setDetail(detail);

            productRepository.save(product, em);

            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void updateProduct(ProductFormDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ.");
        }
        validateFormDTO(dto, dto.getId());

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Product product = productRepository.findByIdWithDetail(dto.getId(), em)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã bị xóa."));

            if (productRepository.existsBySku(dto.getSku(), dto.getId(), em)) {
                throw new IllegalArgumentException("Mã SKU '" + dto.getSku().trim() + "' đã bị trùng với sản phẩm khác.");
            }

            Category category = categoryRepository.findById(dto.getCategoryId(), em)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục lựa chọn không tồn tại."));

            if (Boolean.FALSE.equals(category.getStatus())) {
                throw new IllegalArgumentException("Danh mục '" + category.getName() + "' đang bị vô hiệu hóa.");
            }

            product.setSku(dto.getSku().trim().toUpperCase());
            product.setName(dto.getName().trim());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
            product.setCategory(category);

            ProductDetail detail = product.getDetail();
            if (detail == null) {
                detail = new ProductDetail();
            }
            detail.setManufacturer(dto.getManufacturer() != null ? dto.getManufacturer().trim() : null);
            detail.setWarrantyMonths(dto.getWarrantyMonths() != null ? dto.getWarrantyMonths() : 0);
            detail.setOrigin(dto.getOrigin() != null ? dto.getOrigin().trim() : null);
            detail.setDescription(dto.getDetailDescription() != null ? dto.getDetailDescription().trim() : null);
            detail.setTechnicalSpec(dto.getTechnicalSpec() != null ? dto.getTechnicalSpec().trim() : null);

            product.setDetail(detail);

            productRepository.update(product, em);

            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void softDeleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product product = productRepository.findByIdWithDetail(id, em)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại id: " + id));

            product.setDeleted(true);
            productRepository.update(product, em);

            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    private void validateFormDTO(ProductFormDTO dto, Long currentProductId) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SKU không được để trống.");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0.");
        }
        if (dto.getQuantity() == null || dto.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho phải lớn hơn hoặc bằng 0.");
        }
        if (dto.getCategoryId() == null || dto.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Danh mục là bắt buộc.");
        }
        if (dto.getWarrantyMonths() != null && dto.getWarrantyMonths() < 0) {
            throw new IllegalArgumentException("Thời gian bảo hành không được nhỏ hơn 0 tháng.");
        }
    }
}
