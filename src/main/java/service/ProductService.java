package service;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Category;
import entity.Product;
import entity.ProductDetail;
import repository.CategoryRepository;
import repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepository = new ProductRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Product> searchProducts(ProductSearchDTO criteria) {
        return productRepository.search(criteria);
    }

    public long countProducts(ProductSearchDTO criteria) {
        return productRepository.count(criteria);
    }

    public Product getById(Long id) {
        return productRepository.findByIdWithDetail(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã bị xóa!"));
    }

    public void saveProduct(ProductFormDTO dto) {
        validateForm(dto);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại!"));

        if (!category.getStatus()) {
            throw new IllegalArgumentException("Không thể gán sản phẩm vào danh mục đang bị vô hiệu hóa (Inactive)!");
        }

        Product product;
        ProductDetail detail;

        if (dto.getId() != null) {
            product = getById(dto.getId());
            detail = product.getDetail();
        } else {
            product = new Product();
            detail = new ProductDetail();
        }

        // Map Product
        product.setSku(dto.getSku().trim());
        product.setName(dto.getName().trim());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setStatus(dto.getStatus());
        product.setCategory(category);

        // Map Detail
        detail.setManufacturer(dto.getManufacturer());
        detail.setWarrantyMonths(dto.getWarrantyMonths());
        detail.setOrigin(dto.getOrigin());
        detail.setDescription(dto.getDescription());
        detail.setTechnicalSpec(dto.getTechnicalSpec());

        // Bidirectional Association
        product.setDetail(detail);

        // Atomic persist inside 1 Transaction
        productRepository.save(product);
    }

    public void softDelete(Long id) {
        Product product = getById(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    private void validateForm(ProductFormDTO dto) {
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SKU không được để trống!");
        }
        if (productRepository.existsBySku(dto.getSku().trim(), dto.getId())) {
            throw new IllegalArgumentException("Mã SKU '" + dto.getSku() + "' đã tồn tại trên hệ thống!");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0!");
        }
        if (dto.getQuantity() == null || dto.getQuantity() < 0) {
            throw new IllegalArgumentException("Tồn kho phải lớn hơn hoặc bằng 0!");
        }
        if (dto.getWarrantyMonths() != null && dto.getWarrantyMonths() < 0) {
            throw new IllegalArgumentException("Thời gian bảo hành không được nhỏ hơn 0!");
        }
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn danh mục cho sản phẩm!");
        }
    }
}